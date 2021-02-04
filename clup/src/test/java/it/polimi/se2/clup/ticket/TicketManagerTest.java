package it.polimi.se2.clup.ticket;

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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class TicketManagerTest {
    static TicketManager tm;
    static TicketDataAccess tda;
    static BuildingDataAccess bda;
    static UserDataAccessImpl uda;
    static QueueManager qm;
    private static int unregID1;
    private static int unregID2;
    private static int unregID3;

    @BeforeEach
    public void setup() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("clupTest");
        EntityManager em = emf.createEntityManager();

        tm = new TicketManager();

        bda = new BuildingDataAccess();
        uda = new UserDataAccessImpl();
        qm = new QueueManager();

        tda = tm.getTicketDataAccess();

        uda.em = em;
        bda.em = em;
        tda.em = em;

        tm.getBuildingManager().setDataAccess(bda);
        tm.getBuildingManager().setQueueManager(qm);
        tm.getBuildingManager().getQueueManager().setDataAccess(bda);
        tm.getBuildingManager().getQueueManager().getDataAccess().em = em;

        removeAllFromDatabase(em);

        em.getTransaction().begin();

        //Creation of unregistered customers
        unregID1 = uda.insertUnregisteredAppCustomer();
        unregID2 = uda.insertUnregisteredAppCustomer();
        unregID3 = uda.insertUnregisteredAppCustomer();

        Map<String, Integer> surplus = new HashMap<>();

        //Creation of a building
        int buildingID = bda.insertBuilding(
                "EsselungaStore",
                LocalTime.of(8, 0, 0),
                LocalTime.of(21, 0, 0),
                "via Roma,1",
                3,
                surplus,
                "AccessCODE");

        //considering full building
        bda.retrieveBuilding(buildingID).setActualCapacity(0);

        //Creation of  3 lineUp tickets, one for each unregistered customer
        tm.acquireUnregCustomerLineUpTicket(unregID1, buildingID);
        tm.acquireUnregCustomerLineUpTicket(unregID2, buildingID);
        tm.acquireUnregCustomerLineUpTicket(unregID3, buildingID);

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
    public void checkTicketExpired() throws NotInQueueException {

        LineUpDigitalTicket ticket = tm.getTicketsUnregisteredCustomer(unregID1).get(0);
        tm.validateTicket(ticket.getTicketID());

        ticket.setValidationTime(LocalDateTime.now().minus(Duration.ofMinutes(11)));
        Map<LineUpDigitalTicket, Duration> waitingTimes = tm.getWaitingUpdateUnregCustomer(unregID1);

        Assertions.assertNotNull(waitingTimes);
        Assertions.assertEquals(waitingTimes.get(ticket), Duration.ZERO);
        Assertions.assertEquals(ticket.getState(), TicketState.EXPIRED);
    }

    @Test
    public void correctValidityCheck() {

        LineUpDigitalTicket ticket = tm.getTicketsUnregisteredCustomer(unregID2).get(0);

        Assertions.assertEquals(ticket.getState(), TicketState.INVALID);
        Assertions.assertFalse(tm.validityCheck(ticket.getTicketID()));

        tm.validateTicket(ticket.getTicketID());
        Assertions.assertTrue(tm.validityCheck(ticket.getTicketID()));

        ticket.setState(TicketState.EXPIRED);
        Assertions.assertFalse(tm.validityCheck(ticket.getTicketID()));
    }

    @Test
    public void correctWaitingTimeInQueue() throws NotInQueueException {

        //Setting the delta exit time, all tickets are related to the same building

        Duration deltaExitTime = Duration.ofMinutes(10);

        LineUpDigitalTicket firstInQueue = tm.getTicketsUnregisteredCustomer(unregID1).get(0);

        Map<LineUpDigitalTicket, Duration> waitingTimes = tm.getWaitingUpdateUnregCustomer(unregID1);
        Building building = firstInQueue.getBuilding();
        building.setDeltaExitTime(deltaExitTime);

        //In the setup we have 3 tickets in queue
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
        Assertions.assertEquals(waitingTimes.get(firstInQueue).toMinutes(), TicketManager.extraTime);

        //TODO: control after one customer enters the building, the updated waiting times
    }

}
