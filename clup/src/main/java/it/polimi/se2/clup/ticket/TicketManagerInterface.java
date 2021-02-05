package it.polimi.se2.clup.ticket;

import it.polimi.se2.clup.data.entities.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TicketManagerInterface {

    void acquireStoreManagerTicket(int userID, int buildingID);

    boolean acquireBookingTicket(int userID, int buildingID, LocalDate date, int timeSlotID, int timeSlotLength, List<Department> departments);

    boolean acquireUnregCustomerLineUpTicket(int userID, int buildingID);

    boolean acquireRegCustomerLineUpTicket(int userID, int buildingID);

    List<BookingDigitalTicket> getBookingTicketsRegCustomer(int userID);
    List<LineUpDigitalTicket> getLineUpTicketsRegCustomer (int userID);
    List<LineUpDigitalTicket> getTicketsUnregisteredCustomer (int userID);
    List<LineUpDigitalTicket> getLineUpTicketsStoreManager (int userID);

    void setTicketState(int ticketID, TicketState state);

    Map<LineUpDigitalTicket, Duration> getWaitingUpdateRegCustomer(int userID) throws NotInQueueException;
    Map<LineUpDigitalTicket, Duration> getWaitingUpdateSM(int userID) throws NotInQueueException;
    Map<LineUpDigitalTicket, Duration> getWaitingUpdateUnregCustomer(int userID) throws NotInQueueException;
}
