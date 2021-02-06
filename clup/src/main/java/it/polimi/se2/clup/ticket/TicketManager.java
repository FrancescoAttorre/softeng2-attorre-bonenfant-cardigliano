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
     * This private function returns the number of valid ticket already aquired for a given building
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
     *
     * @param userID
     * @param buildingID
     * @param date
     * @param timeSlotID
     * @param timeSlotLength
     * @param departments departments chosen by the client
     * @return
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

                if (checkTimeSlotsOverlaps(bookingTickets, ticket))
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

    @Override
    public LineUpDigitalTicket acquireRegCustomerLineUpTicket(int userID, int buildingID, boolean buildingIsFull) throws InvalidTicketInsertionException {

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

    @Override
    public List<BookingDigitalTicket> getBookingTicketsRegCustomer(int userID) {

        return ticketDataAccess.retrieveBookingTicketsRegCustomer(userID);
    }

    @Override
    public List<LineUpDigitalTicket> getLineUpTicketsRegCustomer(int userID) {
        return ticketDataAccess.retrieveLineUpTicketsRegCustomer(userID);
    }

    @Override
    public List<LineUpDigitalTicket> getTicketsUnregisteredCustomer(int userID) {
        return ticketDataAccess.retrieveTicketsUnregisteredCustomer(userID);
    }

    @Override
    public List<LineUpDigitalTicket> getLineUpTicketsStoreManager(int userID) {
        return ticketDataAccess.retrieveLineUpTicketsStoreManager(userID);
    }

    @Override
    public Map<LineUpDigitalTicket, Duration> getWaitingUpdateRegCustomer(int userID) throws NotInQueueException {
        List<LineUpDigitalTicket> customerLineUpTickets = ticketDataAccess.retrieveLineUpTicketsRegCustomer(userID);
        Map<LineUpDigitalTicket, Duration> lineUpWaitingTimes = new HashMap<>();

        for (LineUpDigitalTicket ticket : customerLineUpTickets)
            lineUpWaitingTimes.put(ticket, buildingManager.computeWaitingTime(ticket));

        return lineUpWaitingTimes;
    }

    @Override
    public Map<LineUpDigitalTicket, Duration> getWaitingUpdateSM(int userID) throws NotInQueueException {
        List<LineUpDigitalTicket> customerLineUpTickets = ticketDataAccess.retrieveLineUpTicketsStoreManager(userID);
        Map<LineUpDigitalTicket, Duration> lineUpWaitingTimes = new HashMap<>();

        for (LineUpDigitalTicket ticket : customerLineUpTickets) {
            lineUpWaitingTimes.put(ticket, buildingManager.computeWaitingTime(ticket));
        }
        return lineUpWaitingTimes;
    }

    @Override
    public Map<LineUpDigitalTicket, Duration> getWaitingUpdateUnregCustomer(int userID) throws NotInQueueException {

        List<LineUpDigitalTicket> customerLineUpTickets = ticketDataAccess.retrieveTicketsUnregisteredCustomer(userID);
        Map<LineUpDigitalTicket, Duration> lineUpWaitingTimes = new HashMap<>();

        for (LineUpDigitalTicket ticket : customerLineUpTickets)
            lineUpWaitingTimes.put(ticket, buildingManager.computeWaitingTime(ticket));

        return lineUpWaitingTimes;
    }

    @Override
    public boolean validateTicket(int ticketID) {
        return ticketDataAccess.updateTicketState(ticketID, TicketState.VALID);
    }

    @Override
    public boolean validityCheck(int ticketID) {

        LocalDateTime validationTime = ticketDataAccess.retrieveValidationTime(ticketID);

        if (validationTime != null && (Duration.between(validationTime, LocalDateTime.now()).toMinutes() > 10))
            ticketDataAccess.updateTicketState(ticketID, TicketState.EXPIRED);

        return ticketDataAccess.retrieveTicketState(ticketID).equals(TicketState.VALID);
    }

    @Override
    public boolean setTicketState(int ticketID, TicketState state) {
        return ticketDataAccess.updateTicketState(ticketID, state);
    }

}
