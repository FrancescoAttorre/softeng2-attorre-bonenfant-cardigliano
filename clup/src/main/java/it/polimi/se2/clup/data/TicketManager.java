package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketManager implements TicketManagerInterface {

    private TicketDataAccess ticketDataAccess;

    public TicketDataAccess getTicketDataAccess() {
        return ticketDataAccess;
    }

    public void setTicketDataAccess(TicketDataAccess ticketDataAccess) {
        this.ticketDataAccess = ticketDataAccess;
    }

    @Override
    public void acquireStoreManagerTicket(int userID) {
        ticketDataAccess.insertStoreManagerLineUpTicket(userID);
    }

    @Override
    public void acquireBookingTicket(int userID, int buildingID, LocalDate date, int timeSlotID, int timeSlotLength) {
        ticketDataAccess.insertBookingTicket(userID, buildingID, date, timeSlotID, timeSlotLength);
    }

    @Override
    public void acquireRegCustomerLineUpTicket(int userID, int buildingID) {
        ticketDataAccess.insertRegCustomerLineUpTicket(userID, buildingID);
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
    public Map<LineUpDigitalTicket, Duration> getWaitingUpdateRegCustomer(int userID) {
        List<LineUpDigitalTicket> customerLineUpTickets = ticketDataAccess.retrieveLineUpTicketsRegCustomer(userID);
        Map<LineUpDigitalTicket, Duration> lineUpWaitingTimes = new HashMap<>();

        for (LineUpDigitalTicket ticket : customerLineUpTickets) {
            lineUpWaitingTimes.put(ticket, computeWaitingTime(ticket));
        }

        return lineUpWaitingTimes;
    }

    @Override
    public Map<LineUpDigitalTicket, Duration> getWaitingUpdateSM(int userID) {
        List<LineUpDigitalTicket> customerLineUpTickets = ticketDataAccess.retrieveLineUpTicketsStoreManager(userID);
        Map<LineUpDigitalTicket, Duration> lineUpWaitingTimes = new HashMap<>();

        for (LineUpDigitalTicket ticket : customerLineUpTickets) {
            lineUpWaitingTimes.put(ticket, computeWaitingTime(ticket));
        }
        return lineUpWaitingTimes;
    }

    @Override
    public Map<LineUpDigitalTicket, Duration> getWaitingUpdateUnregCustomer(int userID) {
        List<LineUpDigitalTicket> customerLineUpTickets = ticketDataAccess.retrieveTicketsUnregisteredCustomer(userID);
        Map<LineUpDigitalTicket, Duration> lineUpWaitingTimes = new HashMap<>();

        for (LineUpDigitalTicket ticket : customerLineUpTickets) {
            lineUpWaitingTimes.put(ticket, computeWaitingTime(ticket));
        }
        return lineUpWaitingTimes;
    }

    private Duration computeWaitingTime (LineUpDigitalTicket ticket) {
        Duration additionalTime = Duration.ofMinutes(10);
        ticketDataAccess.retrieveAcquisitionTime(ticket);

        if (ticket.getState() == TicketState.VALID)
            return Duration.ZERO;

        if (ticket.getQueue().getQueueTickets().get(0) == ticket && ticket.getState() != TicketState.VALID )
            return ticket.getEstimatedWaitingTime().plus(additionalTime);
        return null;
    }

    @Override
    public void acquireUnregCustomerLineUpTicket(int userID, int buildingID) {
        ticketDataAccess.insertUnregCustomerLineUpTicket(userID, buildingID);
    }

    @Override
    public void setTicketState(int ticketID, TicketState state) {
        ticketDataAccess.updateTicketState(ticketID, state);
    }

}
