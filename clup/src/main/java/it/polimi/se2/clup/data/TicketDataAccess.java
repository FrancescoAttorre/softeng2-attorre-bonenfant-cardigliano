package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.*;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TicketDataAccess component which has to add/retrieve to/from the database tickets and the information related to them,
 * information necessary to the TicketManager
 */
@Stateless
public class TicketDataAccess implements TicketDataAccessInterface {

    @PersistenceContext(unitName = "clup")
    public EntityManager em;

    /**
     * Inserts a line up ticket for the unregistered app customer and building passed as parameters
     */
    @Override
    public LineUpDigitalTicket insertUnregCustomerLineUpTicket(int userID, int buildingID) throws NoResultException {
        LineUpDigitalTicket newTicket = new LineUpDigitalTicket();

        Building building = em.find(Building.class, buildingID);
        building.addTicket(newTicket);

        UnregisteredAppCustomer owner = em.find(UnregisteredAppCustomer.class, userID);
        owner.addLineUpTicket(newTicket);
        newTicket.setUnregisteredOwner(owner);

        newTicket.setAcquisitionTime(LocalDateTime.now());
        newTicket.setState(TicketState.INVALID);
        newTicket.setBuilding(building);

        em.persist(newTicket);
        return newTicket;
    }

    /**
     * Inserts a line up ticket for the registered app customer and building passed as parameters
     */
    @Override
    public LineUpDigitalTicket insertRegCustomerLineUpTicket(int userID, int buildingID) throws NoResultException {
        LineUpDigitalTicket newTicket = new LineUpDigitalTicket();

        Building building = em.find(Building.class, buildingID);
        building.addTicket(newTicket);

        RegisteredAppCustomer owner = em.find(RegisteredAppCustomer.class, userID);
        newTicket.setRegisteredOwner(owner);
        owner.addLineUpTicket(newTicket);

        newTicket.setAcquisitionTime(LocalDateTime.now());
        newTicket.setState(TicketState.INVALID);
        newTicket.setBuilding(building);

        em.persist(newTicket);
        return newTicket;
    }

    /**
     * Inserts a line up ticket for the store manager and building passed as parameter
     */
    @Override
    public LineUpDigitalTicket insertStoreManagerLineUpTicket(int userID) throws NoResultException {

        LineUpDigitalTicket newTicket = new LineUpDigitalTicket();
        PhysicalTicket newPhysicalTicket = new PhysicalTicket();

        StoreManager owner = em.find(StoreManager.class, userID);
        Building building = owner.getBuilding();
        building.addTicket(newTicket);

        owner.addLineUpTicket(newTicket);
        owner.addPhysicalTicket(newPhysicalTicket);

        newPhysicalTicket.setStoreManager(owner);
        newPhysicalTicket.setAssociatedDigitalTicket(newTicket);

        newTicket.setStoreManagerOwner(owner);
        newTicket.setAcquisitionTime(LocalDateTime.now());
        newTicket.setState(TicketState.INVALID);
        newTicket.setAssociatedPhysicalTicket(newPhysicalTicket);
        newTicket.setBuilding(building);

        em.persist(newPhysicalTicket);
        em.persist(newTicket);
        return newTicket;
    }

    /**
     * Inserts a booking ticket for the registered app customer and building passed as parameter,
     * having as parameters also all the information necessary to create a new booking ticket
     */
    @Override
    public BookingDigitalTicket insertBookingTicket(int userID, int buildingID, LocalDate date, int timeSlotID,
                                                    int timeSlotLength, List<Department> chosenDepartments) throws InvalidDepartmentException {

        BookingDigitalTicket newTicket = new BookingDigitalTicket();

        RegisteredAppCustomer owner = em.find(RegisteredAppCustomer.class, userID);

        owner.addBookingTicket(newTicket);

        Building building = em.find(Building.class, buildingID);

        //
        building.addTicket(newTicket);
        //

        newTicket.setOwner(owner);
        newTicket.setState(TicketState.INVALID);
        newTicket.setBuilding(building);


        newTicket.setTimeSlotID(timeSlotID);
        newTicket.setDate(date);
        newTicket.setTimeSlotLength(timeSlotLength);

        List<Department> referencedDept = new ArrayList<>();

        if (chosenDepartments != null) {

            for (Department dep : chosenDepartments) {

                Department dept = em.find(Department.class, dep.getDepartmentID());

                if (!(building.getDepartments().contains(dept)))
                    throw new InvalidDepartmentException();

                dept.addTicket(newTicket);
                referencedDept.add(dept);
            }
        }

        newTicket.setDepartments(referencedDept);
        em.persist(newTicket);
        return newTicket;
    }

    /**
     * Changes the state of the ticket to the value of the state parameter
     * @return true if the change was successful
     */
    @Override
    public boolean updateTicketState(int ticketID, TicketState state) {
         DigitalTicket ticketToUpdate = em.createNamedQuery("DigitalTicket.retrieveTicketById", DigitalTicket.class)
                .setParameter("ticketID", ticketID)
                .getSingleResult();
         if (ticketToUpdate != null) {
             ticketToUpdate.setState(state);
             if (state.equals(TicketState.VALID))
                 ticketToUpdate.setValidationTime(LocalDateTime.now());
             return true;
         }
         return false;
    }

    /**
     * Retrieves all booking tickets already entered in the dataBase for the registered customer given as parameter
     */
    @Override
    public List<BookingDigitalTicket> retrieveBookingTicketsRegCustomer(int userID) throws NoResultException {
        RegisteredAppCustomer registeredAppCustomer = em.find(RegisteredAppCustomer.class, userID);
        if (registeredAppCustomer != null)
            return registeredAppCustomer.getBookingDigitalTickets();
        else
            return null;
    }

    /**
     * Retrieves all line up tickets already entered in the dataBase for the registered customer given as parameter
     */
    @Override
    public List<LineUpDigitalTicket> retrieveLineUpTicketsRegCustomer(int userID) throws NoResultException {
        RegisteredAppCustomer registeredAppCustomer = em.find(RegisteredAppCustomer.class, userID);
        if (registeredAppCustomer != null)
            return registeredAppCustomer.getLineUpDigitalTickets();
        else
            return null;
    }

    /**
     * Retrieves all line up tickets already entered in the dataBase for the unregistered customer given as parameter
     */
    @Override
    public List<LineUpDigitalTicket> retrieveTicketsUnregisteredCustomer(int userID) throws NoResultException {
        UnregisteredAppCustomer unregisteredAppCustomer = em.find(UnregisteredAppCustomer.class, userID);
        if (unregisteredAppCustomer != null)
            return unregisteredAppCustomer.getLineUpDigitalTickets();
        else
            return null;
    }

    /**
     * Retrieves all line up tickets already entered in the dataBase for the store manager given as parameter
     */
    @Override
    public List<LineUpDigitalTicket> retrieveLineUpTicketsStoreManager(int userID) throws NoResultException {
        StoreManager storeManager = em.find(StoreManager.class, userID);
        if (storeManager != null)
            return storeManager.getLineUpDigitalTickets();
        else
            return null;
    }

    /**
     * Retrieves all line up tickets present in the dataBase for a given building
     */
    @Override
    public List<LineUpDigitalTicket> retrieveAllLineUpTickets(int buildingID) {

        return em.createNamedQuery("LineUpDigitalTicket.findAllForBuilding", LineUpDigitalTicket.class)
                .setParameter("buildingID", buildingID)
                .getResultList();
    }


    /**
     * Retrieves the validationTime of a given ticket
    */
    @Override
    public LocalDateTime retrieveValidationTime (int ticketID) {
        DigitalTicket ticket = em.find(DigitalTicket.class, ticketID);
        return ticket.getValidationTime();
    }

    /**
     * Retrieves the state of a given ticket
     */
    @Override
    public TicketState retrieveTicketState (int ticketID) {
        DigitalTicket ticket = em.find(DigitalTicket.class, ticketID);
        return ticket.getState();
    }

    /**
     * Retrieves the capacity of a given building
     */
    @Override
    public int retrieveCapacity (int buildingID) {
        Building building = em.find(Building.class, buildingID);
        return building.getCapacity();
    }
}
