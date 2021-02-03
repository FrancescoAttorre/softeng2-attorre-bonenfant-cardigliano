package it.polimi.se2.clup.ticket;

import it.polimi.se2.clup.building.QueueManager;
import it.polimi.se2.clup.data.BuildingDataAccess;
import it.polimi.se2.clup.data.TicketDataAccess;
import it.polimi.se2.clup.data.UserDataAccessImpl;
import it.polimi.se2.clup.data.entities.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class TicketManagerTest {
    static TicketManager tm;
    static TicketDataAccess tda;
    static BuildingDataAccess bda;
    static UserDataAccessImpl uda;
    static QueueManager qm;
    private static int unregID;

    @BeforeEach
    public void setup() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("clupTest");
        EntityManager em = emf.createEntityManager();

        tm = new TicketManager();

        tda = new TicketDataAccess();
        bda = new BuildingDataAccess();
        uda = new UserDataAccessImpl();
        qm = new QueueManager();
        uda.em = em;
        bda.em = em;
        tda.em = em;

        tm.setTicketDataAccess(tda);
        tm.getBuildingManager().setQueueManager(qm);
        tm.getBuildingManager().getQueueManager().getDataAccess().em = em;

        removeAllFromDatabase(em);

        em.getTransaction().begin();

        //Creation of an unregistered customer
        unregID = uda.insertUnregisteredAppCustomer();

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

        //Creation of 2 tickets
        tm.acquireUnregCustomerLineUpTicket(unregID, buildingID);
        tm.acquireUnregCustomerLineUpTicket(unregID, buildingID);

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
    public void updateWaitingTime() throws NotInQueueException {
        Map<LineUpDigitalTicket, Duration> waitingTimes =  tm.getWaitingUpdateUnregCustomer(unregID);

        Assertions.assertNotNull(waitingTimes);
        for (Duration d: waitingTimes.values())
            Assertions.assertTrue(d.toMinutes() > 0);
    }

}
