package it.polimi.se2.clup.ticket;

import it.polimi.se2.clup.building.BuildingManager;
import it.polimi.se2.clup.data.InvalidDepartmentException;
import it.polimi.se2.clup.data.TicketDataAccess;
import it.polimi.se2.clup.data.entities.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TicketManager implements TicketManagerInterface {

    public static final int extraTime = 10;
    public static final int defaultWaitingTime = 15;

    private TicketDataAccess ticketDataAccess;
    private BuildingManager buildingManager;

    public BuildingManager getBuildingManager() {
        return buildingManager;
    }

    public void setBuildingManager(BuildingManager buildingManager) {
        this.buildingManager = buildingManager;
    }

    public TicketDataAccess getTicketDataAccess() {
        return ticketDataAccess;
    }

    public void setTicketDataAccess(TicketDataAccess ticketDataAccess) {
        this.ticketDataAccess = ticketDataAccess;
    }

    @Override
    public void acquireStoreManagerTicket(int userID, int buildingID) {

        LineUpDigitalTicket newTicket = ticketDataAccess.insertStoreManagerLineUpTicket(userID);

        if (buildingManager.checkBuildingNotFull(buildingID)) {
            validateTicket(newTicket.getTicketID());
            buildingManager.reduceCapacity(buildingID);
        }
        else
            buildingManager.insertInQueue(newTicket);
    }

    @Override
    public boolean acquireBookingTicket(int userID, int buildingID, LocalDate date, int timeSlotID,
                                        int timeSlotLength, List<Department> departments) {

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
    public boolean acquireRegCustomerLineUpTicket(int userID, int buildingID) {

        List<LineUpDigitalTicket> lineUpTickets = ticketDataAccess.retrieveLineUpTicketsRegCustomer(userID);
        if (lineUpTickets != null) {

            for (LineUpDigitalTicket ticket : lineUpTickets) {

                if (ticket.getBuilding().getBuildingID() == buildingID && !ticket.getState().equals(TicketState.EXPIRED))
                    return false;
            }
        }

        LineUpDigitalTicket newTicket = ticketDataAccess.insertRegCustomerLineUpTicket(userID, buildingID);

        if (buildingManager.checkBuildingNotFull(buildingID)) {
            validateTicket(newTicket.getTicketID());
            buildingManager.reduceCapacity(buildingID);
        }
        else
            buildingManager.insertInQueue(newTicket);
        return true;
    }

    @Override
    public boolean acquireUnregCustomerLineUpTicket(int userID, int buildingID) {

        List<LineUpDigitalTicket> lineUpTickets = ticketDataAccess.retrieveTicketsUnregisteredCustomer(userID);
        if (lineUpTickets != null) {
            for (LineUpDigitalTicket ticket : lineUpTickets) {
                if (ticket.getBuilding().getBuildingID() == buildingID)
                    return false;
            }
        }

        LineUpDigitalTicket newTicket = ticketDataAccess.insertUnregCustomerLineUpTicket(userID, buildingID);

        if (buildingManager.checkBuildingNotFull(buildingID)) {
            validateTicket(newTicket.getTicketID());
            buildingManager.reduceCapacity(buildingID);
        }
        else
            buildingManager.insertInQueue(newTicket);
        return true;
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

        for (LineUpDigitalTicket ticket : customerLineUpTickets) {
            lineUpWaitingTimes.put(ticket, computeWaitingTime(ticket));
        }

        return lineUpWaitingTimes;
    }

    @Override
    public Map<LineUpDigitalTicket, Duration> getWaitingUpdateSM(int userID) throws NotInQueueException {
        List<LineUpDigitalTicket> customerLineUpTickets = ticketDataAccess.retrieveLineUpTicketsStoreManager(userID);
        Map<LineUpDigitalTicket, Duration> lineUpWaitingTimes = new HashMap<>();

        for (LineUpDigitalTicket ticket : customerLineUpTickets) {
            lineUpWaitingTimes.put(ticket, computeWaitingTime(ticket));
        }
        return lineUpWaitingTimes;
    }

    @Override
    public Map<LineUpDigitalTicket, Duration> getWaitingUpdateUnregCustomer(int userID) throws NotInQueueException {

        List<LineUpDigitalTicket> customerLineUpTickets = ticketDataAccess.retrieveTicketsUnregisteredCustomer(userID);
        Map<LineUpDigitalTicket, Duration> lineUpWaitingTimes = new HashMap<>();

        for (LineUpDigitalTicket ticket : customerLineUpTickets)
            lineUpWaitingTimes.put(ticket, computeWaitingTime(ticket));

        return lineUpWaitingTimes;
    }

    @Override
    public void validateTicket(int ticketID) {
        ticketDataAccess.updateTicketState(ticketID, TicketState.VALID);
    }

    @Override
    public boolean validityCheck(int ticketID) {

        LocalDateTime validationTime = ticketDataAccess.retrieveValidationTime(ticketID);

        if (validationTime != null && (Duration.between(validationTime, LocalDateTime.now()).toMinutes() > 10))
            ticketDataAccess.updateTicketState(ticketID, TicketState.EXPIRED);

        return ticketDataAccess.retrieveTicketState(ticketID).equals(TicketState.VALID);
    }

    /**
     * ComputeWaitingTime sets the waiting time to zero for valid tickets, changes their state to
     * expired when the ticket are valid from more than 10 minutes,
     * for non valid tickets, instead, it updates the estimated waiting time, basing on the last exit
     * from the ticket's building while in queue, the current time and the medium waiting time for that building
     *
     * In case of delays, the waiting time is fixed to the default waiting time
     *
     * @return new value of estimated waiting time with a precision of minutes
     * @throws NotInQueueException if the ticket is not found in the related queue
     */

    private Duration computeWaitingTime (LineUpDigitalTicket ticket) throws NotInQueueException {

        Duration newWaitingTime;

        switch (ticket.getState()) {
            case EXPIRED:
                newWaitingTime = Duration.ZERO;
                break;

            case VALID:
                if (Duration.between(ticket.getValidationTime(), LocalDateTime.now()).toMinutes() > 10)
                    ticketDataAccess.updateTicketState(ticket.getTicketID(), TicketState.EXPIRED);
                newWaitingTime = Duration.ZERO;
                break;

            case INVALID:
                Duration additionalTime = Duration.ofMinutes(extraTime);
                LocalTime now = LocalTime.now();

                int positionInQueue = ticket.getQueue().getQueueTickets().indexOf(ticket);

                if (positionInQueue != -1) {

                    long averageWait = ticket.getBuilding().getDeltaExitTime().toMinutes();
                    LocalTime lastExitTime = ticket.getBuilding().getLastExitTime();
                    LocalTime acquisitionTime = ticket.getAcquisitionTime().toLocalTime();

                    if (lastExitTime != null && lastExitTime.isAfter(acquisitionTime)) {

                        newWaitingTime = Duration.ofMinutes((averageWait * (positionInQueue + 1)) -
                                Duration.between(lastExitTime, now).toMinutes());
                    }

                    else if (!ticket.getBuilding().getDeltaExitTime().equals(Duration.ZERO)) {

                        newWaitingTime = Duration.ofMinutes(averageWait * (positionInQueue + 1) -
                                Duration.between(acquisitionTime, now).toMinutes());
                    }

                    else
                        newWaitingTime = Duration.ofMinutes((long) defaultWaitingTime * (positionInQueue + 1) -
                                Duration.between(acquisitionTime, now).toMinutes());

                    //
                    if (newWaitingTime.toMinutes() <= 0) {
                        newWaitingTime = additionalTime;
                    }

                } else
                    throw new NotInQueueException();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + ticket.getState());
        }
        return newWaitingTime;
    }

    @Override
    public void setTicketState(int ticketID, TicketState state) {
        ticketDataAccess.updateTicketState(ticketID, state);
    }

}
