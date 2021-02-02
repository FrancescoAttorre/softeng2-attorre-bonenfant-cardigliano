package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketDataAccessTest {

    private static TicketDataAccess dm;
    private static int unregID;
    private static int buildingID;
    private static int smID;

    @BeforeAll
    public static void setup() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("clupTest");
        dm = new TicketDataAccess();
        UserDataAccessImpl uda = new UserDataAccessImpl();
        BuildingDataAccess bdm = new BuildingDataAccess();
        EntityManager em = emf.createEntityManager();
        dm.em = em;
        uda.em = em;
        bdm.em = em;

        em.getTransaction().begin();

        //Creation of an unregistered customer
        unregID = uda.insertUnregisteredAppCustomer();

        //Creation of a registered customer
        uda.insertUser("user", "password");

        //Creation of a sample building

        Map<String,Integer> surplus = new HashMap<>();
        surplus.put("Macelleria",1);
        surplus.put("Pescheria",3);

        buildingID = bdm.insertBuilding(
                "EsselungaStore",
                LocalTime.of(8,0,0),
                LocalTime.of(21,0,0),
                "via Roma,1",
                3,
                surplus,
                "ESSL9821");

        smID = uda.insertStoreManager("ESSL9821");

        em.getTransaction().commit();
    }


    @Test
    public void storeManagerTicketsEnteredProperly() {

        dm.em.getTransaction().begin();

        LineUpDigitalTicket newTicket = dm.insertStoreManagerLineUpTicket(smID);

        dm.em.getTransaction().commit();

        Query query = dm.em.createNamedQuery("LineUpDigitalTicket.selectWithSMID").setParameter("SMID", smID);
        List<LineUpDigitalTicket> result = query.getResultList();
        Assertions.assertTrue(result.contains(newTicket));

        Assertions.assertNotNull(newTicket.getAssociatedPhysicalTicket());
        Assertions.assertNotNull(newTicket.getStoreManagerOwner());
        Assertions.assertNotNull(newTicket.getAcquisitionTime());
        Assertions.assertNotNull(newTicket.getQueue());
        Assertions.assertNotNull(newTicket.getBuilding());
        Assertions.assertEquals(newTicket.getQueue(), newTicket.getBuilding().getQueue());
        Assertions.assertNull(newTicket.getRegisteredOwner());
        Assertions.assertNull(newTicket.getUnregisteredOwner());

        Assertions.assertTrue(dm.retrieveLineUpTicketsStoreManager(smID).contains(newTicket));
    }

    @Test
    public void unregCustomerLineUpTicketEnteredProperly() {

        dm.em.getTransaction().begin();

        LineUpDigitalTicket newTicket = dm.insertUnregCustomerLineUpTicket(unregID, buildingID);

        dm.em.getTransaction().commit();

        Query query = dm.em.createNamedQuery("LineUpDigitalTicket.selectWithUnregID").setParameter("unregID", unregID);
        List<LineUpDigitalTicket> result = query.getResultList();
        Assertions.assertTrue(result.contains(newTicket));

        Assertions.assertNull(newTicket.getAssociatedPhysicalTicket());
        Assertions.assertNull(newTicket.getStoreManagerOwner());
        Assertions.assertNull(newTicket.getRegisteredOwner());
        Assertions.assertNotNull(newTicket.getUnregisteredOwner());
        Assertions.assertNotNull(newTicket.getAcquisitionTime());
        Assertions.assertNotNull(newTicket.getQueue());
        Assertions.assertNotNull(newTicket.getBuilding());
        Assertions.assertEquals(newTicket.getBuilding().getBuildingID(), buildingID);
        Assertions.assertEquals(newTicket.getUnregisteredOwner().getId(), unregID);
        Assertions.assertEquals(newTicket.getQueue(), newTicket.getBuilding().getQueue());

        List<LineUpDigitalTicket> tickets = dm.retrieveTicketsUnregisteredCustomer(unregID);

        Assertions.assertTrue(dm.retrieveTicketsUnregisteredCustomer(unregID).contains(newTicket));
    }

    @Test
    public void bookingTicketEnteredProperly() {

        dm.em.getTransaction().begin();

        int regID = dm.em.createNamedQuery("RegisteredAppCustomer.findUserByUsername",
                RegisteredAppCustomer.class).setParameter("username", "user").getSingleResult().getId();

        //Booking ticket from 8:00 a.m. to 9:00 a.m.
        List<String> chosenDepartments = new ArrayList<>();
        chosenDepartments.add("Macelleria");
        BookingDigitalTicket newTicket = dm.insertBookingTicket(regID, buildingID, LocalDate.now(), 32, 4, chosenDepartments);

        dm.em.getTransaction().commit();

        Query query = dm.em.createNamedQuery("BookingDigitalTicket.selectWithRegID").setParameter("regID", regID);
        List<BookingDigitalTicket> result = query.getResultList();
        Assertions.assertTrue(result.contains(newTicket));

        Assertions.assertNotNull(newTicket.getOwner());
        Assertions.assertNotNull(newTicket.getDate());
        Assertions.assertNotNull(newTicket.getBuilding());
        Assertions.assertEquals(newTicket.getBuilding().getBuildingID(), buildingID);

        Assertions.assertEquals(newTicket.getOwner().getId(), regID);

        Assertions.assertTrue(dm.retrieveBookingTicketsRegCustomer(regID).contains(newTicket));
    }

    @Test
    public void ticketStateUpdate() {
        LineUpDigitalTicket newTicket = dm.insertUnregCustomerLineUpTicket(unregID, buildingID);
        dm.updateTicketState(newTicket.getTicketID(), TicketState.VALID);

        Assertions.assertEquals(newTicket.getState(), TicketState.VALID);
        Assertions.assertNotNull(newTicket.getValidationTime());
    }
}
