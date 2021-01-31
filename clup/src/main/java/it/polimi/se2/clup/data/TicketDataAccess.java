package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.*;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public class TicketDataAccess implements TicketDataAccessInterface {

    @PersistenceContext(unitName = "clup")
    protected EntityManager em;

    @Override
    public LineUpDigitalTicket insertUnregCustomerLineUpTicket(int userID, int buildingID) throws NoResultException {
        LineUpDigitalTicket newTicket = new LineUpDigitalTicket();

        Building building = em.find(Building.class, buildingID);

        UnregisteredAppCustomer owner = em.find(UnregisteredAppCustomer.class, userID);
        newTicket.setUnregisteredOwner(owner);

        newTicket.setAcquisitionTime(LocalDateTime.now());
        newTicket.setState(TicketState.INVALID);
        newTicket.setBuilding(building);
        newTicket.setQueue(building.getQueue());

        em.persist(newTicket);
        return newTicket;
    }

    @Override
    public LineUpDigitalTicket insertRegCustomerLineUpTicket(int userID, int buildingID) throws NoResultException {
        LineUpDigitalTicket newTicket = new LineUpDigitalTicket();

        Building building = em.find(Building.class, buildingID);

        RegisteredAppCustomer owner = em.find(RegisteredAppCustomer.class, userID);
        newTicket.setRegisteredOwner(owner);
        owner.addLineUpTicket(newTicket);

        newTicket.setAcquisitionTime(LocalDateTime.now());
        newTicket.setState(TicketState.INVALID);
        newTicket.setBuilding(building);
        newTicket.setQueue(building.getQueue());

        em.persist(newTicket);
        return newTicket;
    }

    @Override
    public LineUpDigitalTicket insertStoreManagerLineUpTicket(int userID) throws NoResultException {
        LineUpDigitalTicket newTicket = new LineUpDigitalTicket();
        PhysicalTicket newPhysicalTicket = new PhysicalTicket();

        StoreManager owner = em.find(StoreManager.class, userID);
        Building building = owner.getBuilding();

        owner.addLineUpTicket(newTicket);
        owner.addPhysicalTicket(newPhysicalTicket);

        newPhysicalTicket.setStoreManager(owner);
        newPhysicalTicket.setAssociatedDigitalTicket(newTicket);
        newTicket.setQueue(building.getQueue());

        newTicket.setStoreManagerOwner(owner);
        newTicket.setAcquisitionTime(LocalDateTime.now());
        newTicket.setState(TicketState.INVALID);
        newTicket.setAssociatedPhysicalTicket(newPhysicalTicket);
        newTicket.setBuilding(building);

        em.persist(newPhysicalTicket);
        em.persist(newTicket);
        return newTicket;
    }

    @Override
    public BookingDigitalTicket insertBookingTicket(int userID, int buildingID, LocalDate date, int timeSlotID, int timeSlotLength)
            throws NoResultException {

        BookingDigitalTicket newTicket = new BookingDigitalTicket();

        RegisteredAppCustomer owner = em.find(RegisteredAppCustomer.class, userID);
        owner.addBookingTicket(newTicket);
        Building building = em.find(Building.class, buildingID);

        newTicket.setOwner(owner);
        newTicket.setState(TicketState.INVALID);
        newTicket.setBuilding(building);
        newTicket.setTimeSlotID(timeSlotID);
        newTicket.setDate(date);
        newTicket.setTimeSlotLength(timeSlotLength);

        em.persist(newTicket);
        return newTicket;
    }

    @Override
    public void updateTicketState(int ticketID, TicketState state) throws NoResultException {
         DigitalTicket ticketToUpdate = em.createNamedQuery("DigitalTicket.retrieveTicketById", DigitalTicket.class)
                .setParameter("ticketID", ticketID)
                .getSingleResult();
         ticketToUpdate.setState(state);
         ticketToUpdate.setValidationTime(LocalDateTime.now());
    }

    @Override
    public List<BookingDigitalTicket> retrieveBookingTicketsRegCustomer(int userID) throws NoResultException {
        RegisteredAppCustomer registeredAppCustomer = em.find(RegisteredAppCustomer.class, userID);
        return registeredAppCustomer.getBookingDigitalTickets();
    }

    @Override
    public List<LineUpDigitalTicket> retrieveLineUpTicketsRegCustomer(int userID) throws NoResultException {
        RegisteredAppCustomer registeredAppCustomer = em.find(RegisteredAppCustomer.class, userID);
        return registeredAppCustomer.getLineUpDigitalTickets();
    }

    @Override
    public List<LineUpDigitalTicket> retrieveTicketsUnregisteredCustomer(int userID) throws NoResultException {
        UnregisteredAppCustomer unregisteredAppCustomer = em.find(UnregisteredAppCustomer.class, userID);
        return unregisteredAppCustomer.getLineUpDigitalTickets();
    }

    @Override
    public List<LineUpDigitalTicket> retrieveLineUpTicketsStoreManager(int userID) throws NoResultException {
        StoreManager storeManager = em.find(StoreManager.class, userID);
        return storeManager.getLineUpDigitalTickets();
    }

    @Override
    public LocalDateTime retrieveAcquisitionTime (LineUpDigitalTicket lineUpTicket) {
        return lineUpTicket.getAcquisitionTime();
    }
}
