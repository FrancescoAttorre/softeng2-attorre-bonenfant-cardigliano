package it.polimi.se2.clup.ticket;

import it.polimi.se2.clup.building.BuildingManager;
import it.polimi.se2.clup.building.WaitingTimeInt;
import it.polimi.se2.clup.data.InvalidDepartmentException;
import it.polimi.se2.clup.data.TicketDataAccess;
import it.polimi.se2.clup.data.TicketDataAccessInterface;
import it.polimi.se2.clup.data.entities.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class of the component with the aim of managing tickets, acquiring a new one, obtaining existing ones,
 * changing their status, validate them.
 */
@Stateless
public class TicketManager implements TicketManagerInterface, TicketValidationInt {

    @EJB private TicketDataAccessInterface ticketDataAccess;
    @EJB private WaitingTimeInt buildingManager;

    public void setBuildingManager(BuildingManager buildingManager) {
        this.buildingManager = buildingManager;
    }

    public void setTicketDataAccess(TicketDataAccess ticketDataAccess) {
        this.ticketDataAccess = ticketDataAccess;
    }

    /**
     * This private function returns the number of valid ticket already acquired for a given building
     * @param buildingID id of the building
     */
    private int computeNumValidTickets(int buildingID) {
        int numValidLineUpTickets = 0;
        for (LineUpDigitalTicket t : ticketDataAccess.retrieveAllLineUpTickets(buildingID)) {

            if (t.getState().equals(TicketState.VALID))
                numValidLineUpTickets ++;
        }
        return numValidLineUpTickets;
    }

    /**
     * Method to acquire a ticket for a store manager. The ticket is validated if the building has still free
     * places and the tickets already validated for such building doesn't exceed the building capacity
     * @param userID id of the store manager
     * @param buildingID id of the related building
     */
    @Override
    public LineUpDigitalTicket acquireStoreManagerTicket(int userID, int buildingID, boolean buildingIsFull ) {

        LineUpDigitalTicket newTicket = ticketDataAccess.insertStoreManagerLineUpTicket(userID);

        if (!buildingIsFull &&
                computeNumValidTickets(buildingID) < ticketDataAccess.retrieveCapacity(buildingID)) {
            validateTicket(newTicket.getTicketID());
            return null;
        }
        else
            return newTicket;
    }

    /**
     * This method controls whether the time slots of the booking ticket to acquire overlap with the ones of another
     * booking ticket of the same customer of the same day
     * @param bookingTickets all the tickets taken by a registered user, even of different buildings
     * @param ticket single ticket to compare
     * @return true if the tickets overlap
     */
    private boolean checkTimeSlotsOverlaps(List<BookingDigitalTicket> bookingTickets, BookingDigitalTicket ticket) {

        for (BookingDigitalTicket ticketToCompare : bookingTickets) {

            if (!ticketToCompare.equals(ticket)) {

                int startingTimeSlot = ticket.getTimeSlotID();
                int endingTimeSlot = ticket.getTimeSlotID() + ticket.getTimeSlotLength();
                int startingTimeSlotToCompare = ticketToCompare.getTimeSlotID();
                int endingTimeSlotToCompare = ticketToCompare.getTimeSlotID() + ticketToCompare.getTimeSlotLength();

                if ((startingTimeSlotToCompare > startingTimeSlot && startingTimeSlotToCompare < endingTimeSlot) ||
                        (startingTimeSlotToCompare < endingTimeSlot && endingTimeSlotToCompare > startingTimeSlot) ||
                        (startingTimeSlotToCompare < startingTimeSlot && endingTimeSlotToCompare > startingTimeSlot))
                    return true;
            }
        }
        return false;
    }

    /**
     * Method to acquire a booking ticket given date, a time slot id to indicate the start of the reservation
     * throughout the day, the number of slots to indicate the permanence time and optionally the specific departments
     * chosen to visit
     * @return true if the acquisition was successful
     */
    @Override
    public boolean acquireBookingTicket(int userID, int buildingID, LocalDate date, int timeSlotID,
                                        int timeSlotLength, List<Department> departments) throws Exception {

        if (date == null)
            return false;

        List<BookingDigitalTicket> bookingTickets = ticketDataAccess.retrieveBookingTicketsRegCustomer(userID);
        if (bookingTickets != null) {

            for (BookingDigitalTicket ticket : bookingTickets) {

                if (ticket.getBuilding().getBuildingID() == buildingID &&
                        (ticket.getDate().getDayOfMonth() == date.getDayOfMonth() && ticket.getDate().getMonth() == date.getMonth()
                        && ticket.getDate().getYear() == date.getYear()))
                    return false;

                if (ticket.getDate().getDayOfMonth() == date.getDayOfMonth() && ticket.getDate().getMonth() == date.getMonth()
                        && ticket.getDate().getYear() == date.getYear() && checkTimeSlotsOverlaps(bookingTickets, ticket))
                    return false;
            }
        }
        try {
            ticketDataAccess.insertBookingTicket(userID, buildingID, date, timeSlotID, timeSlotLength, departments);
        }
        catch (InvalidDepartmentException invalidDep) {
            return false;
        }
        return true;
    }

    /**
     * Method to acquire a line up ticket with as owner a registered customer
     * @param userID ID of the registered customer
     * @param buildingID ID of the building for which he wants to queue
     * @param buildingIsFull true if the building is already full, so the customer has to wait in queue
     * @return the new ticket acquired only if it has to be put in queue
     * @throws InvalidTicketInsertionException whether is already present a ticket for the same building, of the same user
     */
    @Override
    public LineUpDigitalTicket acquireRegCustomerLineUpTicket(int userID, int buildingID, boolean buildingIsFull)
            throws InvalidTicketInsertionException {

        List<LineUpDigitalTicket> lineUpTickets = ticketDataAccess.retrieveLineUpTicketsRegCustomer(userID);
        if (lineUpTickets != null) {

            for (LineUpDigitalTicket ticket : lineUpTickets) {

                if (ticket.getBuilding().getBuildingID() == buildingID && !ticket.getState().equals(TicketState.EXPIRED))
                    throw new InvalidTicketInsertionException();
            }
        }

        LineUpDigitalTicket newTicket = ticketDataAccess.insertRegCustomerLineUpTicket(userID, buildingID);

        if (!buildingIsFull &&
                computeNumValidTickets(buildingID) < ticketDataAccess.retrieveCapacity(buildingID)) {
            validateTicket(newTicket.getTicketID());
            return null;
        }
        else
            return newTicket;
    }

    /**
     * Method to acquire a line up ticket with as owner an unregistered customer
     * @param userID ID of the registered customer
     * @param buildingID ID of the building for which he wants to queue
     * @param buildingIsFull true if the building is already full, so the customer has to wait in queue
     * @return the new ticket acquired only if it has to be put in queue
     * @throws InvalidTicketInsertionException whether is already present a ticket for the same building, of the same user
     */
    @Override
    public LineUpDigitalTicket acquireUnregCustomerLineUpTicket(int userID, int buildingID, boolean buildingIsFull) throws InvalidTicketInsertionException {

        List<LineUpDigitalTicket> lineUpTickets = ticketDataAccess.retrieveTicketsUnregisteredCustomer(userID);
        if (lineUpTickets != null) {
            for (LineUpDigitalTicket ticket : lineUpTickets) {
                if (ticket.getBuilding().getBuildingID() == buildingID)
                    throw new InvalidTicketInsertionException();
            }
        }

        LineUpDigitalTicket newTicket = ticketDataAccess.insertUnregCustomerLineUpTicket(userID, buildingID);

        if (!buildingIsFull &&
                computeNumValidTickets(buildingID) < ticketDataAccess.retrieveCapacity(buildingID)) {
            validateTicket(newTicket.getTicketID());
            return null;
        }
        else
            return newTicket;
    }

    /**
     * @param userID ID of the user whose tickets I want to get
     * @return list ok booking tickets of the user
     */
    @Override
    public List<BookingDigitalTicket> getBookingTicketsRegCustomer(int userID) {

        return ticketDataAccess.retrieveBookingTicketsRegCustomer(userID);
    }

    /**
     * @param userID ID of the registered customer whose tickets I want to get
     * @return list of booking tickets of the user
     */
    @Override
    public List<LineUpDigitalTicket> getLineUpTicketsRegCustomer(int userID) {
        return ticketDataAccess.retrieveLineUpTicketsRegCustomer(userID);
    }

    /**
     * @param userID ID of the unregistered customer whose tickets I want to get
     * @return list of line up tickets of the user
     */
    @Override
    public List<LineUpDigitalTicket> getTicketsUnregisteredCustomer(int userID) {
        return ticketDataAccess.retrieveTicketsUnregisteredCustomer(userID);
    }

    /**
     * @param userID ID of the store manager whose tickets I want to get
     * @return list of line up tickets of the user
     */
    @Override
    public List<LineUpDigitalTicket> getLineUpTicketsStoreManager(int userID) {
        return ticketDataAccess.retrieveLineUpTicketsStoreManager(userID);
    }

    /**
     * Discovery method which has to be called periodically bu registered customers to be updated on the waiting times
     * for the owned tickets
     * @param userID ID of the user who made the request
     * @return map of line up tickets owned by the user with the related waiting time
     * @throws NotInQueueException if some ticket isn't in queue
     */
    @Override
    public Map<LineUpDigitalTicket, Duration> getWaitingUpdateRegCustomer(int userID) throws NotInQueueException {
        List<LineUpDigitalTicket> customerLineUpTickets = ticketDataAccess.retrieveLineUpTicketsRegCustomer(userID);
        Map<LineUpDigitalTicket, Duration> lineUpWaitingTimes = new HashMap<>();

        for (LineUpDigitalTicket ticket : customerLineUpTickets)
            lineUpWaitingTimes.put(ticket, buildingManager.computeWaitingTime(ticket));

        return lineUpWaitingTimes;
    }

    /**
     * Discovery method which has to be called periodically by store managers to be updated on the waiting times for
     * the owned tickets
     * @param userID ID of the user who made the request
     * @return map of line up tickets owned by the user with the related waiting time
     * @throws NotInQueueException if some ticket isn't in queue
     */
    @Override
    public Map<LineUpDigitalTicket, Duration> getWaitingUpdateSM(int userID) throws NotInQueueException {
        List<LineUpDigitalTicket> customerLineUpTickets = ticketDataAccess.retrieveLineUpTicketsStoreManager(userID);
        Map<LineUpDigitalTicket, Duration> lineUpWaitingTimes = new HashMap<>();

        for (LineUpDigitalTicket ticket : customerLineUpTickets) {
            lineUpWaitingTimes.put(ticket, buildingManager.computeWaitingTime(ticket));
        }
        return lineUpWaitingTimes;
    }

    /**
     * Discovery method which has to be called periodically by unregistered customers to be updated on the waiting times
     * for the owned tickets
     * @param userID ID of the user who made the request
     * @return map of line up tickets owned by the user with the related waiting time
     * @throws NotInQueueException if some ticket isn't in queue
     */
    @Override
    public Map<LineUpDigitalTicket, Duration> getWaitingUpdateUnregCustomer(int userID) throws NotInQueueException {

        List<LineUpDigitalTicket> customerLineUpTickets = ticketDataAccess.retrieveTicketsUnregisteredCustomer(userID);
        Map<LineUpDigitalTicket, Duration> lineUpWaitingTimes = new HashMap<>();

        for (LineUpDigitalTicket ticket : customerLineUpTickets)
            lineUpWaitingTimes.put(ticket, buildingManager.computeWaitingTime(ticket));

        return lineUpWaitingTimes;
    }

    /**
     * @param ticketID ID of the ticket whose state must be set to valid
     * @return true if it was successful
     */
    @Override
    public boolean validateTicket(int ticketID) {
        return ticketDataAccess.updateTicketState(ticketID, TicketState.VALID);
    }

    /**
     * This method sets the ticket state to expired if it is valid from more than 10 minutes
     * @param ticketID ID of the ticket to control
     * @return true if the ticket is valid
     */
    @Override
    public boolean validityCheck(int ticketID) {

        LocalDateTime validationTime = ticketDataAccess.retrieveValidationTime(ticketID);

        if (validationTime != null && (Duration.between(validationTime, LocalDateTime.now()).toMinutes() > 10))
            ticketDataAccess.updateTicketState(ticketID, TicketState.EXPIRED);

        return ticketDataAccess.retrieveTicketState(ticketID).equals(TicketState.VALID);
    }

    /**
     * Sets the state of the ticket related to ticketID to the state given as parameter
     */
    @Override
    public boolean setTicketState(int ticketID, TicketState state) {
        return ticketDataAccess.updateTicketState(ticketID, state);
    }

}
