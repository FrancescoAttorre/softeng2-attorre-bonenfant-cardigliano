package it.polimi.se2.clup.building;

import it.polimi.se2.clup.auth.AuthManager;
import it.polimi.se2.clup.data.BuildingDataAccess;
import it.polimi.se2.clup.data.TicketDataAccess;
import it.polimi.se2.clup.data.UserDataAccess;
import it.polimi.se2.clup.data.entities.*;
import it.polimi.se2.clup.ticket.InvalidTicketInsertionException;
import it.polimi.se2.clup.ticket.TicketManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class BuildingManagerTest {
    static BuildingManager bm;
    static TicketManager tm;
    static QueueManager qm;
    static TimeSlotManager tsm;
    static AuthManager am;
    static BuildingDataAccess buildingDataAccess;
    static UserDataAccess userDataAccess;

    private int buildingID;
    private int regUserId;
    private int secondUserId;
    private int activityId;
    private int firstTicketID;
    private int secondTicketID;

    @BeforeEach
    public void setup() throws Exception {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("clupTest");
        EntityManager em = emf.createEntityManager();

        bm = new BuildingManager();
        tm = new TicketManager();
        buildingDataAccess = new BuildingDataAccess();
        buildingDataAccess.em = em;
        bm.setDataAccess(buildingDataAccess);

        userDataAccess = new UserDataAccess();
        am = new AuthManager();
        userDataAccess.em = em;
        am.setUserDAO(userDataAccess);

        TicketDataAccess ticketDataAccess = new TicketDataAccess();
        ticketDataAccess.em = em;
        tm.setTicketDataAccess(ticketDataAccess);
        //// why double arrows
        tm.setBuildingManager(bm);
        bm.setTicketManager(tm);

        qm = new QueueManager();
        qm.setDataAccess(buildingDataAccess);
        bm.setQueueManager(qm);

        tsm = new TimeSlotManager();
        tsm.setDataAccess(buildingDataAccess);
        bm.setTimeSlotManager(tsm);

        removeAllFromDatabase(em);

        em.getTransaction().begin();
        userDataAccess.insertActivity("EsselungaActivity","PIVAEsselunga","EsselungaPassword");
        activityId = userDataAccess.retrieveActivity("PIVAEsselunga").getId();

        int firstUserId = userDataAccess.insertUnregisteredAppCustomer();
        secondUserId = userDataAccess.insertUnregisteredAppCustomer();

        userDataAccess.insertUser("firstReg","firstPass");

        Map<String,Integer> surplus = new HashMap<>();
        surplus.put("Macelleria",10);
        surplus.put("Pescheria",1);
        surplus.put("Bevande",12);

        buildingDataAccess.insertBuilding(
                activityId,
                "EsselungaStore",
                LocalTime.of(8, 0, 0),
                LocalTime.of(21, 0, 0),
                "via Roma,1",
                4,
                surplus,
                "AccessCODE");

        buildingID = buildingDataAccess.retrieveBuilding("EsselungaStore").getBuildingID();


        //considering an empty building
        //Creation of 2 tickets
        tm.acquireUnregCustomerLineUpTicket(firstUserId, buildingID, false);
        tm.acquireUnregCustomerLineUpTicket(secondUserId, buildingID, false);

        firstTicketID = buildingDataAccess.em.createNamedQuery("LineUpDigitalTicket.selectWithUnregID", LineUpDigitalTicket.class)
                .setParameter("unregID", firstUserId).getResultList().get(0).getTicketID();
        secondTicketID = buildingDataAccess.em.createNamedQuery("LineUpDigitalTicket.selectWithUnregID", LineUpDigitalTicket.class)
                .setParameter("unregID", secondUserId).getResultList().get(0).getTicketID();

        bm.customerEntry(firstTicketID, buildingID, firstUserId, null);
        bm.customerEntry(secondTicketID, buildingID, secondUserId, null);

        em.getTransaction().commit();
        em.getTransaction().begin();

        //acquire ticket with time slot of 12.00 that is 12*60/15 = 48 and for only one slot for all departments
        RegisteredAppCustomer registeredAppCustomer = em.createNamedQuery("RegisteredAppCustomer.findUserByUsername",RegisteredAppCustomer.class)
                .setParameter("username","firstReg").getSingleResult();
        regUserId = registeredAppCustomer.getId();


        Building building = buildingDataAccess.retrieveBuilding(buildingID);
        List<Integer> depIDs = new ArrayList<>();
        for (Department dep : building.getDepartments())
            depIDs.add(dep.getDepartmentID());

        tm.acquireBookingTicket(registeredAppCustomer.getId(),
                buildingID,
                LocalDate.ofInstant(Instant.now(),ZoneId.systemDefault()),
                48,
                1,
                depIDs);

        em.getTransaction().commit();
    }

    private void removeAllFromDatabase(EntityManager em) {
        em.getTransaction().begin();

        for(BookingDigitalTicket b : em.createNamedQuery("BookingDigitalTicket.findAll",BookingDigitalTicket.class).getResultList()){
            em.remove(b);
        }

        for(RegisteredAppCustomer u : em.createNamedQuery("RegisteredAppCustomer.findAll",RegisteredAppCustomer.class).getResultList()){
            em.remove(u);
        }
        for(UnregisteredAppCustomer u : em.createNamedQuery("UnregisteredAppCustomer.findAll",UnregisteredAppCustomer.class).getResultList()){
            em.remove(u);
        }
        for(StoreManager u : em.createNamedQuery("StoreManager.findAll",StoreManager.class).getResultList()){
            em.remove(u);
        }
        for(Building b : em.createNamedQuery("Building.findAll",Building.class).getResultList()){
            em.remove(b);
        }
        for(Activity a : em.createNamedQuery("Activity.selectAll",Activity.class).getResultList()){
            em.remove(a);
        }
        em.getTransaction().commit();
    }

    @Test
    public void shouldUpdateStatistics() {
        buildingDataAccess.em.getTransaction().begin();

        LocalTime oldLastExitTime = buildingDataAccess.retrieveBuilding(buildingID).getLastExitTime();
        Duration oldLastDeltaExitTime = buildingDataAccess.retrieveBuilding(buildingID).getDeltaExitTime();

        bm.customerExit(buildingID, firstTicketID);

        //SHOULD UPDATE STATISTICS
        Assertions.assertNotEquals(oldLastDeltaExitTime,buildingDataAccess.retrieveBuilding(buildingID).getDeltaExitTime());
        Assertions.assertNotEquals(oldLastExitTime,buildingDataAccess.retrieveBuilding(buildingID).getLastExitTime());

        buildingDataAccess.em.getTransaction().commit();
    }

    @Test
    public void shouldValidateNextTicket(){
        buildingDataAccess.em.getTransaction().begin();

        bm.customerExit(buildingID, firstTicketID);

        //SHOULD VALIDATE NEXT
        Assertions.assertEquals(TicketState.VALID,tm.getTicketsUnregisteredCustomer(secondUserId).get(0).getState());

        buildingDataAccess.em.getTransaction().commit();
    }

    @Test
    public void shouldRemoveFromQueueNextTicket() throws InvalidTicketInsertionException {
        buildingDataAccess.em.getTransaction().begin();

        //the building in setup had first and second users inside, full
        int thirdUserId = userDataAccess.insertUnregisteredAppCustomer();
        int fourthUserId = userDataAccess.insertUnregisteredAppCustomer();

        //two tickets in queue
        Assertions.assertNotNull(tm.acquireUnregCustomerLineUpTicket(thirdUserId, buildingID, true));
        Assertions.assertNotNull(tm.acquireUnregCustomerLineUpTicket(fourthUserId, buildingID, true));
        LineUpDigitalTicket thirdTicket = buildingDataAccess.em.createNamedQuery("LineUpDigitalTicket.selectWithUnregID", LineUpDigitalTicket.class)
                .setParameter("unregID", thirdUserId).getResultList().get(0);
        LineUpDigitalTicket fourthTicket = buildingDataAccess.em.createNamedQuery("LineUpDigitalTicket.selectWithUnregID", LineUpDigitalTicket.class)
                .setParameter("unregID", fourthUserId).getResultList().get(0);
        bm.insertInQueue(thirdTicket);
        bm.insertInQueue(fourthTicket);

        bm.customerExit(buildingID,firstTicketID);

        //SHOULD REMOVE FROM QUEUE
        Assertions.assertEquals(buildingDataAccess.retrieveTicketsInQueue(buildingID).size(), 1);
        Assertions.assertEquals(buildingDataAccess.retrieveTicketsInQueue(buildingID).get(0).getUnregisteredOwner().getId(), fourthUserId);

        buildingDataAccess.em.getTransaction().commit();
    }


    @Test
    public void shouldInsertBuilding(){
        buildingDataAccess.em.getTransaction().begin();

        boolean ret = bm.insertBuilding(
                activityId,
                "IkeaMilano",
                LocalTime.of(8,0,0),
                LocalTime.of(20,0,0),
                "via Poeti,1",
                500,
                null,
                "IkeaAccessCode");

        Assertions.assertTrue(ret);

        // INDEX 1 IF THERE IS ONLY ONE BUILDING INSERTED IN SETUP
        Assertions.assertEquals("IkeaMilano",buildingDataAccess.retrieveBuildings().get(1).getName());

        buildingDataAccess.em.getTransaction().commit();

    }

    @Test
    public void shouldNotInsertBuildingWithMissingParameters(){
        buildingDataAccess.em.getTransaction().begin();

        boolean ret = bm.insertBuilding(
                activityId,
                "IkeaMilano",
                LocalTime.of(8,0,0),
                LocalTime.of(20,0,0),
                null,
                500,
                null,
                "IkeaAccessCode");

        Assertions.assertFalse(ret);
        Assertions.assertEquals(1,buildingDataAccess.retrieveBuildings().size());

        buildingDataAccess.em.getTransaction().commit();

    }

    @Test
    public void shouldNotInsertBuildingWithAlreadyPickedCode(){
        buildingDataAccess.em.getTransaction().begin();

        boolean ret = bm.insertBuilding(
                activityId,
                "IkeaMilano",
                LocalTime.of(8,0,0),
                LocalTime.of(20,0,0),
                "via Poesia,1",
                500,
                null,
                "AccessCODE");

        Assertions.assertFalse(ret);
        Assertions.assertEquals(1,buildingDataAccess.retrieveBuildings().size());

        buildingDataAccess.em.getTransaction().commit();

    }

    @Test
    public void shouldRemoveClosedHoursTimeSlots(){
        buildingDataAccess.em.getTransaction().begin();

        List<Department> departments = buildingDataAccess.retrieveBuilding(buildingID).getDepartments();
        departments.remove(2);
        Map<Department,List<Integer>> timeSlots = bm.getAvailableTimeSlots(
                buildingID,
                LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault()),
                Duration.of(31, ChronoUnit.MINUTES),
                departments);

        Department firstDep = departments.get(0);

        Assertions.assertEquals(32,timeSlots.get(firstDep).get(0));
        Assertions.assertEquals(83,timeSlots.get(firstDep).get(timeSlots.get(firstDep).size() - 1 ));
        // (32,83) compresi
        Assertions.assertEquals(52,timeSlots.get(firstDep).size());


        buildingDataAccess.em.getTransaction().commit();
    }

    @Test
    public void shouldNotReturnTimeSlotOfFullDepartment(){
        buildingDataAccess.em.getTransaction().begin();

        List<Department> departments = buildingDataAccess.retrieveBuilding(buildingID).getDepartments();

        Map<Department,List<Integer>> timeSlots = bm.getAvailableTimeSlots(
                buildingID,
                LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault()),
                Duration.of(31, ChronoUnit.MINUTES),
                departments);

        Department macelleria = departments.get(0);
        Department pescheria = departments.get(2);

        //as setup was already picked the timeSlot number 48
        //should not contains time slot 48
        Assertions.assertFalse(timeSlots.get(pescheria).contains(48));
        //should not contain time slot 47 as well because we are requesting 2 time slot as we want to stay for 31 minutes
        Assertions.assertFalse(timeSlots.get(pescheria).contains(47));

        //check if this two time slots are the only one removed
        Assertions.assertEquals(timeSlots.get(pescheria).size(),timeSlots.get(macelleria).size() - 2 );

        buildingDataAccess.em.getTransaction().commit();
    }

    @Test
    public void shouldPreventCustomerWithInvalidBookingTicketToEnter() {

        BookingDigitalTicket setupTicket = buildingDataAccess.em.createNamedQuery("BookingDigitalTicket.selectWithRegID",BookingDigitalTicket.class)
                .setParameter("regID", regUserId).getResultList().get(0);

        int startingMinute = setupTicket.getTimeSlotID() * BuildingManager.minutesInASlot;
        int hour = startingMinute / 60;
        int minute = startingMinute % 60;

        if (Duration.between(LocalTime.of(hour, minute), LocalTime.now()).toMinutes() > 10 ||
                setupTicket.getDate().getYear() != LocalDateTime.now().getYear() ||
                setupTicket.getDate().getMonthValue() != LocalDateTime.now().getMonthValue() ||
                setupTicket.getDate().getDayOfMonth() != LocalDateTime.now().getDayOfMonth())

            Assertions.assertFalse(bm.customerEntry(setupTicket.getTicketID(), buildingID, regUserId,tm.getBookingTicketsRegCustomer(regUserId)));
    }

    @Test
    public void shouldAllowCustomerWithValidLineUpTicketToEnter() throws InvalidTicketInsertionException {

        buildingDataAccess.em.getTransaction().begin();

        Building building = buildingDataAccess.retrieveBuilding(buildingID);

        int thirdUserId = userDataAccess.insertUnregisteredAppCustomer();
        int fourthUserId = userDataAccess.insertUnregisteredAppCustomer();

        //considering an empty building
        bm.customerExit(buildingID, firstTicketID);
        bm.customerExit(buildingID, secondTicketID);

        Assertions.assertEquals(building.getActualCapacity(), 2);

        //Creation of 2 tickets ready to enter
        tm.acquireUnregCustomerLineUpTicket(thirdUserId, buildingID, false);
        tm.acquireUnregCustomerLineUpTicket(fourthUserId, buildingID, false);

        buildingDataAccess.em.getTransaction().commit();

        LineUpDigitalTicket firstTicket = buildingDataAccess.em.createNamedQuery("LineUpDigitalTicket.selectWithUnregID", LineUpDigitalTicket.class)
                .setParameter("unregID", thirdUserId).getResultList().get(0);

        LineUpDigitalTicket secondTicket = buildingDataAccess.em.createNamedQuery("LineUpDigitalTicket.selectWithUnregID", LineUpDigitalTicket.class)
                .setParameter("unregID", fourthUserId).getResultList().get(0);

        Assertions.assertTrue(bm.customerEntry(firstTicket.getTicketID(), buildingID, thirdUserId, tm.getBookingTicketsRegCustomer(thirdUserId)));
        Assertions.assertTrue(bm.customerEntry(secondTicket.getTicketID(), buildingID, fourthUserId,tm.getBookingTicketsRegCustomer(fourthUserId)));

    }

    @Test
    public void bookingDigitalTicketShouldBeAvailable() {
        buildingDataAccess.em.getTransaction().begin();

        List<Integer> requestedTimeSlots = new ArrayList<>();
        requestedTimeSlots.add(36);

        List<Department> departments = buildingDataAccess.retrieveBuilding(buildingID).getDepartments();

        boolean requestedTimeSlotIsAvailable = bm.checkTicketAvailability(buildingID,LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault()),requestedTimeSlots,departments);

        Assertions.assertTrue(requestedTimeSlotIsAvailable);

        buildingDataAccess.em.getTransaction().commit();
    }

    @Test
    public void bookingDigitalTicketShouldNotBeAvailable(){
        buildingDataAccess.em.getTransaction().begin();

        List<Integer> requestedTimeSlots = new ArrayList<>();
        requestedTimeSlots.add(48);

        List<Department> departments = buildingDataAccess.retrieveBuilding(buildingID).getDepartments();

        boolean requestedTimeSlotIsAvailable = bm.checkTicketAvailability(buildingID,LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault()),requestedTimeSlots,departments);

        Assertions.assertFalse(requestedTimeSlotIsAvailable);

        buildingDataAccess.em.getTransaction().commit();
    }
}
