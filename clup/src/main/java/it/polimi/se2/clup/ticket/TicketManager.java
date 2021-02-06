package it.polimi.se2.clup.ticket;

import it.polimi.se2.clup.building.BuildingManager;
import it.polimi.se2.clup.data.InvalidDepartmentException;
import it.polimi.se2.clup.data.TicketDataAccess;
import it.polimi.se2.clup.data.entities.*;

import javax.ejb.Stateless;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class TicketManager implements TicketManagerInterface, TicketValidationInt {

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
            lineUpWaitingTimes.put(ticket, buildingManager.computeWaitingTime(ticket));
        }

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
