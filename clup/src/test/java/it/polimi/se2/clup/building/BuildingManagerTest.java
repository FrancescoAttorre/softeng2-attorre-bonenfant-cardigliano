package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.BuildingDataAccess;
import it.polimi.se2.clup.data.TicketDataAccess;
import it.polimi.se2.clup.data.UserDataAccessImpl;
import it.polimi.se2.clup.data.entities.*;
import it.polimi.se2.clup.ticket.TicketManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.*;
import java.util.*;

public class BuildingManagerTest {
    static BuildingManager bm;
    static TicketManager tm;
    static QueueManager qm;
    static TimeSlotManager tsm;
    static BuildingDataAccess buildingDataAccess;

    int buildingID;
    int firstUserId;
    int secondUserId;

    @BeforeEach
    public void setup(){

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("clupTest");
        EntityManager em = emf.createEntityManager();

        bm = new BuildingManager();
        tm = new TicketManager();
        buildingDataAccess = new BuildingDataAccess();
        buildingDataAccess.em = em;
        bm.setDataAccess(buildingDataAccess);

        UserDataAccessImpl userDataAccess = new UserDataAccessImpl();
        userDataAccess.em = em;

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


        firstUserId = userDataAccess.insertUnregisteredAppCustomer();
        secondUserId = userDataAccess.insertUnregisteredAppCustomer();

        userDataAccess.insertUser("firstReg","firstPass");

        Map<String,Integer> surplus = new HashMap<>();
        surplus.put("Macelleria",10);
        surplus.put("Pescheria",1);
        surplus.put("Bevande",12);

        buildingID = buildingDataAccess.insertBuilding(
                "EsselungaStore",
                LocalTime.of(8, 0, 0),
                LocalTime.of(21, 0, 0),
                "via Roma,1",
                3,
                surplus,
                "AccessCODE");

        //considering a full building
        buildingDataAccess.retrieveBuilding(buildingID).setActualCapacity(0);

        //Creation of 2 tickets
        tm.acquireUnregCustomerLineUpTicket(firstUserId, buildingID);
        tm.acquireUnregCustomerLineUpTicket(secondUserId, buildingID);


        //acquire ticket with time slot of 12.00 that is 12*60/15 = 48 and for only one slot
        RegisteredAppCustomer registeredAppCustomer = em.createNamedQuery("RegisteredAppCustomer.findUserByUsername",RegisteredAppCustomer.class).setParameter("username","firstReg").getSingleResult();
        /*
        tm.acquireBookingTicket(registeredAppCustomer.getId(),
                buildingID,
                LocalDate.ofInstant(Instant.now(),ZoneId.systemDefault()),
                48,
                1,
                Arrays.asList("Macelleria","Pescheria","Bevande"));
*/

        em.getTransaction().commit();
    }

    private void removeAllFromDatabase(EntityManager em) {
        em.getTransaction().begin();
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
        em.getTransaction().commit();
    }

    @Test
    public void shouldUpdateStatistics() {
        buildingDataAccess.em.getTransaction().begin();

        LocalTime oldLastExitTime = buildingDataAccess.retrieveBuilding(buildingID).getLastExitTime();
        Duration oldLastDeltaExitTime = buildingDataAccess.retrieveBuilding(buildingID).getDeltaExitTime();

        bm.customerExit(buildingID);

        //SHOULD UPDATE STATISTICS
        Assertions.assertNotEquals(oldLastDeltaExitTime,buildingDataAccess.retrieveBuilding(buildingID).getDeltaExitTime());
        Assertions.assertNotEquals(oldLastExitTime,buildingDataAccess.retrieveBuilding(buildingID).getLastExitTime());

        buildingDataAccess.em.getTransaction().commit();
    }

    @Test
    public void shouldValidateNextTicket(){
        buildingDataAccess.em.getTransaction().begin();

        bm.customerExit(buildingID);

        //SHOULD VALIDATE NEXT
        Assertions.assertEquals(TicketState.VALID,tm.getTicketsUnregisteredCustomer(firstUserId).get(0).getState());

        buildingDataAccess.em.getTransaction().commit();
    }



    @Test
    public void shouldRemoveFromQueueNextTicket(){
        buildingDataAccess.em.getTransaction().begin();

        bm.customerExit(buildingID);

        //SHOULD REMOVE FROM QUEUE
        Assertions.assertEquals(buildingDataAccess.retrieveTicketsInQueue(buildingID).size(), 1);
        Assertions.assertEquals(buildingDataAccess.retrieveTicketsInQueue(buildingID).get(0).getUnregisteredOwner().getId(), secondUserId);

        buildingDataAccess.em.getTransaction().commit();
    }


    @Test
    public void shouldInsertBuilding(){
        buildingDataAccess.em.getTransaction().begin();

        boolean ret = bm.insertBuilding("IkeaMilano",
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

        boolean ret = bm.insertBuilding("IkeaMilano",
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

        boolean ret = bm.insertBuilding("IkeaMilano",
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
    public void shouldComputeAvailableTimeSlots(){
        buildingDataAccess.em.getTransaction().begin();

        //bm.getAvailableTimeSlots(buildingID,LocalDate.ofInstant(Instant.now(),ZoneId.systemDefault()),)

        buildingDataAccess.em.getTransaction().commit();
    }

}
