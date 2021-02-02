package it.polimi.se2.clup.ticket;

import it.polimi.se2.clup.data.BuildingDataAccess;
import it.polimi.se2.clup.data.TicketDataAccess;
import it.polimi.se2.clup.data.UserDataAccessImpl;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
    private static int unregID;

    @BeforeAll
    public static void setup() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("clupTest");
        EntityManager em = emf.createEntityManager();

        tm = new TicketManager();

        tda = new TicketDataAccess();
        bda = new BuildingDataAccess();
        uda = new UserDataAccessImpl();
        uda.em = em;
        bda.em = em;
        tda.em = em;

        tm.setTicketDataAccess(tda);
        tm.getBuildingManager().getQueueManager().getDataAccess().em = em;

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

    @Test
    public void updateWaitingTime() throws NotInQueueException {
        Map<LineUpDigitalTicket, Duration> waitingTimes =  tm.getWaitingUpdateUnregCustomer(unregID);

        Assertions.assertNotNull(waitingTimes);
        for (Duration d: waitingTimes.values())
            Assertions.assertTrue(d.toMinutes() > 0);
    }

}
