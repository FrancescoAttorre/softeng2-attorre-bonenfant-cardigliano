package it.polimi.se2.clup.ticket;

import it.polimi.se2.clup.building.BuildingManager;
import it.polimi.se2.clup.data.TicketDataAccess;
import it.polimi.se2.clup.data.entities.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TicketManager implements TicketManagerInterface {

    public static final int extraTime = 10;
    public static final int defaultWaitingTime = 15;

    private TicketDataAccess ticketDataAccess = new TicketDataAccess();
    private BuildingManager buildingManager = new BuildingManager();

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
    public void acquireStoreManagerTicket(int userID) {
        LineUpDigitalTicket newTicket = ticketDataAccess.insertStoreManagerLineUpTicket(userID);
        buildingManager.insertInQueue(newTicket);
    }

    @Override
    public void acquireBookingTicket(int userID, int buildingID, LocalDate date, int timeSlotID,
                                     int timeSlotLength, List<String> departments) {
        ticketDataAccess.insertBookingTicket(userID, buildingID, date, timeSlotID, timeSlotLength, departments);
    }

    @Override
    public void acquireRegCustomerLineUpTicket(int userID, int buildingID) {
        LineUpDigitalTicket newTicket = ticketDataAccess.insertRegCustomerLineUpTicket(userID, buildingID);
        buildingManager.insertInQueue(newTicket);
    }

    @Override
    public void acquireUnregCustomerLineUpTicket(int userID, int buildingID) {
        LineUpDigitalTicket newTicket = ticketDataAccess.insertUnregCustomerLineUpTicket(userID, buildingID);
        buildingManager.insertInQueue(newTicket);
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

    /**
     * ComputeWaitingTime sets the waiting time to zero for valid tickets, changes their state to
     * expired when the ticket are valid from more than 10 minutes,
     * for non valid tickets, instead, it updates the estimated waiting time, basing on the last exit
     * from the ticket's building, the current time and the medium waiting time for that building
     * @return new value of estimated waiting time
     * @throws NotInQueueException if the ticket is not found in the related queue
     */

    private Duration computeWaitingTime (LineUpDigitalTicket ticket) throws NotInQueueException {

        Duration newWaitingTime;

        switch (ticket.getState()) {
            case EXPIRED -> newWaitingTime = Duration.ZERO;
            case VALID -> {
                newWaitingTime = Duration.ZERO;
                if ((ChronoUnit.MINUTES.between(LocalDateTime.now(), ticket.getValidationTime())) > 10)
                    ticketDataAccess.updateTicketState(ticket.getTicketID(), TicketState.EXPIRED);
            }
            case INVALID -> {
                Duration additionalTime = Duration.ofMinutes(extraTime);
                int positionInQueue = ticket.getQueue().getQueueTickets().indexOf(ticket);
                if (positionInQueue != -1) {
                    long averageWait = ticket.getBuilding().getDeltaExitTime().toMinutes();

                    if (ticket.getBuilding().getLastExitTime() != null)
                        newWaitingTime = Duration.ofMinutes((averageWait * (positionInQueue + 1)) -
                            (LocalTime.now().getMinute() - ticket.getBuilding().getLastExitTime().getMinute())); //if more than 1h doesn't work
                    else
                        newWaitingTime = Duration.ofMinutes(defaultWaitingTime -
                                (LocalDateTime.now().getMinute() - ticket.getAcquisitionTime().getMinute()));

                    if (newWaitingTime.toMinutes() <= 0)
                        newWaitingTime = newWaitingTime.plus(additionalTime);
                } else
                    throw new NotInQueueException();
            }
            default -> throw new IllegalStateException("Unexpected value: " + ticket.getState());
        }
        return newWaitingTime;
    }

    @Override
    public void setTicketState(int ticketID, TicketState state) {
        ticketDataAccess.updateTicketState(ticketID, state);
    }

}
