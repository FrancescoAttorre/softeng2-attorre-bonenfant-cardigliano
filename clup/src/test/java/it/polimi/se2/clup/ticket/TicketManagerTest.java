package it.polimi.se2.clup.ticket;

import it.polimi.se2.clup.building.BuildingManager;
import it.polimi.se2.clup.building.QueueManager;
import it.polimi.se2.clup.data.BuildingDataAccess;
import it.polimi.se2.clup.data.TicketDataAccess;
import it.polimi.se2.clup.data.UserDataAccessImpl;
import it.polimi.se2.clup.data.entities.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.*;
import java.util.HashMap;
import java.util.Map;

public class TicketManagerTest {
    static TicketManager tm;
    static BuildingDataAccess bda;
    static UserDataAccessImpl uda;
    static TicketDataAccess tda;
    static QueueManager qm;
    static BuildingManager bm;
    private static int unregID1;
    private static int unregID2;
    private static int unregID3;
    private static int unregID4;
    private static int buildingID;
    private static EntityManager em;

    @BeforeEach
    public void setup() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("clupTest");
        em = emf.createEntityManager();

        tm = new TicketManager();

        bda = new BuildingDataAccess();
        uda = new UserDataAccessImpl();
        tda = new TicketDataAccess();
        qm = new QueueManager();
        bm = new BuildingManager();

        uda.em = em;
        bda.em = em;
        tda.em = em;

        bm.setTicketManager(tm);
        tm.setBuildingManager(bm);
        tm.setTicketDataAccess(tda);

        qm.setDataAccess(bda);
        bm.setDataAccess(bda);
        bm.setQueueManager(qm);


        removeAllFromDatabase(em);

        em.getTransaction().begin();

        uda.insertActivity("EsselungaActivity","PIVAEsselunga","EsselungaPassword");
        int activityId = uda.retrieveActivity("PIVAEsselunga").getId();

        //Creation of unregistered customers
        unregID1 = uda.insertUnregisteredAppCustomer();
        unregID2 = uda.insertUnregisteredAppCustomer();
        unregID3 = uda.insertUnregisteredAppCustomer();
        unregID4 = uda.insertUnregisteredAppCustomer();

        Map<String, Integer> surplus = new HashMap<>();

        //Creation of a building

        bda.insertBuilding(
                activityId,
                    "EsselungaStore",
                    LocalTime.of(8, 0, 0),
                    LocalTime.of(21, 0, 0),
                    "via Roma,1",
                    2,
                    surplus,
                    "AccessCODE");

        buildingID = bda.retrieveBuilding("EsselungaStore").getBuildingID();

        //considering full building
        bda.retrieveBuilding(buildingID).setActualCapacity(0);

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
        for(Activity a : em.createNamedQuery("Activity.selectAll",Activity.class).getResultList()){
            em.remove(a);
        }
        em.getTransaction().commit();
    }

    /**
     * After validating a ticket, this test checks whether, with a validation time older than the
     * the time interval in which a ticket remains valid, the ticket became correctly expired
     * and the waiting time associated set to zero
     */
    @Test
    public void checkTicketExpired() throws NotInQueueException, InvalidTicketInsertionException {

        //Creation of  3 lineUp tickets, one for each unregistered customer
        tm.acquireUnregCustomerLineUpTicket(unregID1, buildingID, false);

        LineUpDigitalTicket ticket = tm.getTicketsUnregisteredCustomer(unregID1).get(0);
        tm.validateTicket(ticket.getTicketID());

        ticket.setValidationTime(LocalDateTime.now().minus(Duration.ofMinutes(11)));
        Map<LineUpDigitalTicket, Duration> waitingTimes = tm.getWaitingUpdateUnregCustomer(unregID1);

        Assertions.assertNotNull(waitingTimes);
        Assertions.assertEquals(waitingTimes.get(ticket), Duration.ZERO);
        Assertions.assertEquals(ticket.getState(), TicketState.EXPIRED);
    }

    /**
     * This test checks whether the validity check works properly, validating an invalid ticket
     * and checking its validity before and after.
     */
    @Test
    public void correctValidityCheck() throws InvalidTicketInsertionException {

        tm.acquireUnregCustomerLineUpTicket(unregID2, buildingID, true);

        LineUpDigitalTicket ticket = tm.getTicketsUnregisteredCustomer(unregID2).get(0);

        Assertions.assertEquals(ticket.getState(), TicketState.INVALID);
        Assertions.assertFalse(tm.validityCheck(ticket.getTicketID()));

        tm.validateTicket(ticket.getTicketID());
        Assertions.assertTrue(tm.validityCheck(ticket.getTicketID()));

        ticket.setState(TicketState.EXPIRED);
        Assertions.assertFalse(tm.validityCheck(ticket.getTicketID()));
    }
    /*
    /**
     * This test checks that the waiting time is computed correctly during the waiting in queue,
     * without anyone entering/leaving the building

    @Test
    public void correctWaitingTimeInQueue() throws NotInQueueException, InvalidTicketInsertionException {

        //Creation of  3 lineUp tickets
        //-----------------------------

        tm.acquireUnregCustomerLineUpTicket(unregID1, buildingID, false);
        tm.acquireUnregCustomerLineUpTicket(unregID2, buildingID, false);
        tm.acquireUnregCustomerLineUpTicket(unregID3, buildingID, true);

        //Setting the delta exit time, all tickets are related to the same building

        Duration deltaExitTime = Duration.ofMinutes(10);

        LineUpDigitalTicket firstInQueue = tm.getTicketsUnregisteredCustomer(unregID1).get(0);

        Building building = firstInQueue.getBuilding();
        building.setDeltaExitTime(deltaExitTime);

        Map<LineUpDigitalTicket, Duration> waitingTimes = tm.getWaitingUpdateUnregCustomer(unregID1);

        //All the tickets are invalid, so they have to wait a time based on deltaExitTime and people with higher priority

        Assertions.assertEquals(waitingTimes.get(firstInQueue).toMinutes(), deltaExitTime.toMinutes());

        LineUpDigitalTicket secondInQueue = tm.getTicketsUnregisteredCustomer(unregID2).get(0);
        waitingTimes = tm.getWaitingUpdateUnregCustomer(unregID2);
        Assertions.assertEquals(waitingTimes.get(secondInQueue).toMinutes(), 2 * deltaExitTime.toMinutes());

        LineUpDigitalTicket lastInQueue = tm.getTicketsUnregisteredCustomer(unregID3).get(0);
        waitingTimes = tm.getWaitingUpdateUnregCustomer(unregID3);
        Assertions.assertEquals(waitingTimes.get(lastInQueue).toMinutes(), 3 * deltaExitTime.toMinutes());

        //The last customer who left the building, left it 15 minutes ago
        // so there's a delay if the first in queue is still invalid
        building.setLastExitTime(LocalTime.now().minus(Duration.ofMinutes(15)));

        waitingTimes = tm.getWaitingUpdateUnregCustomer(unregID1);
        Assertions.assertEquals(waitingTimes.get(firstInQueue).toMinutes(), BuildingManager.extraTime);
    }

    @Test
    public void waitingTimeAfterExit() throws NotInQueueException, InvalidTicketInsertionException {

        //considering an empty building (actualCapacity = capacity)
        Building building = bda.retrieveBuilding(buildingID);
        building.setActualCapacity(building.getCapacity());

        em.getTransaction().begin();

        //Creation of  4 lineUp tickets, one for each unregistered customer
        tm.acquireUnregCustomerLineUpTicket(unregID1, buildingID, false);
        tm.acquireUnregCustomerLineUpTicket(unregID2, buildingID, false);
        tm.acquireUnregCustomerLineUpTicket(unregID3, buildingID, true);
        tm.acquireUnregCustomerLineUpTicket(unregID4, buildingID, true);

        em.getTransaction().commit();

        LineUpDigitalTicket ticket1 = tm.getTicketsUnregisteredCustomer(unregID1).get(0);
        Assertions.assertEquals(ticket1.getState(), TicketState.VALID);
        Assertions.assertEquals(tm.getTicketsUnregisteredCustomer(unregID2).get(0).getState(), TicketState.VALID);
        Assertions.assertEquals(tm.getTicketsUnregisteredCustomer(unregID3).get(0).getState(), TicketState.INVALID);
        Assertions.assertEquals(tm.getTicketsUnregisteredCustomer(unregID4).get(0).getState(), TicketState.INVALID);

        //delta is not set, so it is used the defaultWaitingTime
        LineUpDigitalTicket lastInQueue = tm.getTicketsUnregisteredCustomer(unregID4).get(0);
        Assertions.assertEquals(tm.getWaitingUpdateUnregCustomer(unregID4).get(lastInQueue).toMinutes(),
                2 * BuildingManager.defaultWaitingTime);

        //simulating someone exiting the building 10 minutes ago
        //------------------------------------------------------

        bm.customerExit(buildingID, ticket1.getTicketID()); //customer exit will simulate an exit in the current time

        Assertions.assertEquals(tm.getWaitingUpdateUnregCustomer(unregID4).get(tm.getTicketsUnregisteredCustomer(unregID4).get(0)).toMinutes(),
                BuildingManager.defaultWaitingTime);
        Assertions.assertEquals(tm.getWaitingUpdateUnregCustomer(unregID3).get(tm.getTicketsUnregisteredCustomer(unregID3).get(0)),
                Duration.ZERO);
        Assertions.assertEquals(tm.getTicketsUnregisteredCustomer(unregID3).get(0).getState(), TicketState.VALID);
    }*/

    @Test
    public void bookingsMustNotOverlap () throws Exception {
        em.getTransaction().begin();

        tm.acquireUnregCustomerLineUpTicket(unregID1, buildingID, false);
        uda.insertUser("user","pass");

        int regAppCustomerID = em.createNamedQuery("RegisteredAppCustomer.findUserByUsername",RegisteredAppCustomer.class)
                .setParameter("username","user").getSingleResult().getId();

        Assertions.assertTrue(tm.acquireBookingTicket(regAppCustomerID,
                buildingID,
                LocalDate.ofInstant(Instant.now(),ZoneId.systemDefault()),
                48,
                2,
                null));

        Assertions.assertFalse(tm.acquireBookingTicket(regAppCustomerID,
                buildingID,
                LocalDate.ofInstant(Instant.now(),ZoneId.systemDefault()),
                49,
                1,
                null));

        Assertions.assertFalse(tm.acquireBookingTicket(regAppCustomerID,
                buildingID,
                LocalDate.ofInstant(Instant.now(),ZoneId.systemDefault()),
                45,
                8,
                null));

        em.getTransaction().commit();
    }

    @Test
    public void sameCustomerCannotAcquire2TicketsSameBuildingSameDay () throws Exception {

        em.getTransaction().begin();

        // the first 2 tickets are not to be inserted in queue
        Assertions.assertNull(tm.acquireUnregCustomerLineUpTicket(unregID1, buildingID, false));
        Assertions.assertThrows(InvalidTicketInsertionException.class, () -> tm.acquireUnregCustomerLineUpTicket(unregID1, buildingID, false));

        Assertions.assertTrue(uda.insertUser("user","firstPass"));

        em.getTransaction().commit();

        RegisteredAppCustomer registeredAppCustomer = em.createNamedQuery("RegisteredAppCustomer.findUserByUsername",RegisteredAppCustomer.class)
                .setParameter("username","user").getSingleResult();

        Building building = bda.retrieveBuilding(buildingID);
        Assertions.assertTrue(tm.acquireBookingTicket(registeredAppCustomer.getId(),
                buildingID,
                LocalDate.ofInstant(Instant.now(),ZoneId.systemDefault()),
                48,
                1,
                building.getDepartments()));

        Assertions.assertFalse(tm.acquireBookingTicket(registeredAppCustomer.getId(),
                buildingID,
                LocalDate.ofInstant(Instant.now(),ZoneId.systemDefault()),
                60,
                1,
                building.getDepartments()));
    }

    @Test
    public void shouldNotInsertBookingTicketWithMissingDate() throws Exception {

        tda.em.getTransaction().begin();

        Assertions.assertTrue(uda.insertUser("user","firstPass"));

        RegisteredAppCustomer registeredAppCustomer = em.createNamedQuery("RegisteredAppCustomer.findUserByUsername",RegisteredAppCustomer.class)
                .setParameter("username","user").getSingleResult();

        Assertions.assertFalse(tm.acquireBookingTicket(registeredAppCustomer.getId(),
                buildingID,
                null,
                48,
                1,
                null));

        tda.em.getTransaction().commit();
    }
}
