package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketDataAccessTest {

    private static TicketDataAccess dm;
    private static BuildingDataAccess bdm;
    private static int unregID;
    private static int buildingID;
    private static int smID;

    @BeforeEach
    public void setup() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("clupTest");
        EntityManager em = emf.createEntityManager();

        UserDataAccessImpl uda = new UserDataAccessImpl();
        bdm = new BuildingDataAccess();

        dm = new TicketDataAccess();
        dm.em = em;
        uda.em = em;
        bdm.em = em;

        removeAllFromDatabase(em);

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

    private void removeAllFromDatabase(EntityManager em) {
        em.getTransaction().begin();

        for(StoreManager u : em.createNamedQuery("StoreManager.findAll",StoreManager.class).getResultList()){
            em.remove(u);
        }
        for(RegisteredAppCustomer u : em.createNamedQuery("RegisteredAppCustomer.findAll",RegisteredAppCustomer.class).getResultList()){
            em.remove(u);
        }
        for(UnregisteredAppCustomer u : em.createNamedQuery("UnregisteredAppCustomer.findAll",UnregisteredAppCustomer.class).getResultList()){
            em.remove(u);
        }
        for(Building b : em.createNamedQuery("Building.findAll",Building.class).getResultList()){
            em.remove(b);
        }
        em.getTransaction().commit();
    }

    @Test
    public void storeManagerTicketsEnteredProperly() {

        dm.em.getTransaction().begin();

        LineUpDigitalTicket newTicket = dm.insertStoreManagerLineUpTicket(smID);

        dm.em.getTransaction().commit();

        List<LineUpDigitalTicket> result = dm.em.createNamedQuery("LineUpDigitalTicket.selectWithSMID", LineUpDigitalTicket.class)
                .setParameter("SMID", smID).getResultList();
        Assertions.assertTrue(result.contains(newTicket));

        Assertions.assertNotNull(newTicket.getAssociatedPhysicalTicket());
        Assertions.assertNotNull(newTicket.getStoreManagerOwner());
        Assertions.assertNotNull(newTicket.getAcquisitionTime());
        Assertions.assertNotNull(newTicket.getBuilding());
        if (newTicket.getQueue()!= null)
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

        List<LineUpDigitalTicket> result = dm.em.createNamedQuery("LineUpDigitalTicket.selectWithUnregID", LineUpDigitalTicket.class)
                .setParameter("unregID", unregID).getResultList();

        Assertions.assertTrue(result.contains(newTicket));

        Assertions.assertNull(newTicket.getAssociatedPhysicalTicket());
        Assertions.assertNull(newTicket.getStoreManagerOwner());
        Assertions.assertNull(newTicket.getRegisteredOwner());
        Assertions.assertNotNull(newTicket.getUnregisteredOwner());
        Assertions.assertNotNull(newTicket.getAcquisitionTime());
        Assertions.assertNotNull(newTicket.getBuilding());
        Assertions.assertEquals(newTicket.getBuilding().getBuildingID(), buildingID);
        Assertions.assertEquals(newTicket.getUnregisteredOwner().getId(), unregID);
        if (newTicket.getQueue()!= null)
            Assertions.assertEquals(newTicket.getQueue(), newTicket.getBuilding().getQueue());

        Assertions.assertTrue(dm.retrieveTicketsUnregisteredCustomer(unregID).contains(newTicket));
    }

    @Test
    public void bookingTicketEnteredProperly() throws Exception {

        dm.em.getTransaction().begin();

        int regID = dm.em.createNamedQuery("RegisteredAppCustomer.findUserByUsername",
                RegisteredAppCustomer.class).setParameter("username", "user").getSingleResult().getId();

        //Booking ticket from 8:00 a.m. to 9:00 a.m.
        List<Department> chosenDepartments = new ArrayList<>();

        List<Department> availableDep = bdm.retrieveBuilding(buildingID).getDepartments();

        chosenDepartments.add(availableDep.get(0));
        BookingDigitalTicket newTicket = dm.insertBookingTicket(regID, buildingID, LocalDate.now(), 32, 4, chosenDepartments);

        dm.em.getTransaction().commit();

        List<BookingDigitalTicket> result = dm.em.createNamedQuery("BookingDigitalTicket.selectWithRegID", BookingDigitalTicket.class)
                .setParameter("regID", regID).getResultList();
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
