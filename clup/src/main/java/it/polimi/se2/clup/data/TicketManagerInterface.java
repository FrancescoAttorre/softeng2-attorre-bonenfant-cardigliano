package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TicketManagerInterface {

    void acquireStoreManagerTicket(int userID);

    void acquireBookingTicket(int userID, int buildingID, LocalDate date, int timeSlotID, int timeSlotLength);

    void acquireUnregCustomerLineUpTicket(int userID, int buildingID);

    void acquireRegCustomerLineUpTicket(int userID, int buildingID);

    List<BookingDigitalTicket> getBookingTicketsRegCustomer(int userID);
    List<LineUpDigitalTicket> getLineUpTicketsRegCustomer (int userID);
    List<LineUpDigitalTicket> getTicketsUnregisteredCustomer (int userID);
    List<LineUpDigitalTicket> getLineUpTicketsStoreManager (int userID);

    void setTicketState(int ticketID, TicketState state);

    Map<LineUpDigitalTicket, Duration> getWaitingUpdateRegCustomer(int userID);
    Map<LineUpDigitalTicket, Duration> getWaitingUpdateSM(int userID);
    Map<LineUpDigitalTicket, Duration> getWaitingUpdateUnregCustomer(int userID);
}
