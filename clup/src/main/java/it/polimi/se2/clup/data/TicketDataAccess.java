package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Time;
import java.util.Date;
import java.util.List;

public class TicketDataAccess implements TicketDataAccessInterface {

    @PersistenceContext(unitName = "clup")
    protected EntityManager em;

    @Override
    public void insertCustomerLineUpTicket(int userID, int buildingID) {
        LineUpDigitalTicket newTicket = new LineUpDigitalTicket();


        AppCustomer owner = em.find(AppCustomer.class, userID);
        Building building = em.find(Building.class, buildingID);

        //newTicket.setOwner(owner);
        newTicket.setState(TicketState.INVALID);
        newTicket.setBuilding(building);

        em.persist(newTicket);
    }

    @Override
    public void insertStoreManagerLineUpTicket(int userID) {
        LineUpDigitalTicket newTicket = new LineUpDigitalTicket();
        PhysicalTicket newPhysicalTicket = new PhysicalTicket();

        StoreManager owner = em.find(StoreManager.class, userID);
        Building building = owner.getBuilding();

        newPhysicalTicket.setStoreManager(owner);
        newPhysicalTicket.setAssociatedDigitalTicket(newTicket);

        //newTicket.setOwner(owner);
        newTicket.setState(TicketState.INVALID);
        newTicket.setAssociatedPhysicalTicket(newPhysicalTicket);
        newTicket.setBuilding(building);

        em.persist(newPhysicalTicket);
        em.persist(newTicket);
    }

    @Override
    public void insertBookingTicket(int userID, int buildingID, Date date, TimeSlot timeSlot) {
        BookingDigitalTicket newTicket = new BookingDigitalTicket();

        RegisteredAppCustomer owner = em.find(RegisteredAppCustomer.class, userID);
        Building building = em.find(Building.class, buildingID);

        newTicket.setOwner(owner);
        newTicket.setState(TicketState.INVALID);
        newTicket.setBuilding(building);
        newTicket.setTimeSlot(timeSlot);

        Time startingTime = timeSlot.getStartingTime();
        Time departureTime = timeSlot.getEndingTime();

        newTicket.setArrivalTime(startingTime);
        newTicket.setDepartureTime(departureTime);
        //newTicket.setPermanenceTime();

        em.persist(newTicket);
    }

    @Override
    public void updateTicketState(int ticketID, TicketState state) {
         em.createNamedQuery("DigitalTicket.retrieveTicketById", DigitalTicket.class)
                .setParameter("ticketID", ticketID)
                .getSingleResult().setState(state);
    }

    @Override
    public List<DigitalTicket> retrieveTickets(int userID) {
        User user= em.find(User.class, userID);

        return user.getDigitalTickets();
    }
}
