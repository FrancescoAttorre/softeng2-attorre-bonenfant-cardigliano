package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.BookingDigitalTicket;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;
import it.polimi.se2.clup.data.entities.TicketState;

import java.time.LocalDate;
import java.util.List;

public interface TicketDataAccessInterface {

    void insertUnregCustomerLineUpTicket(int userID, int buildingID);
    void insertRegCustomerLineUpTicket (int userID, int buildingID);
    void insertStoreManagerLineUpTicket (int userID);
    void insertBookingTicket(int userID, int buildingID, LocalDate date, int timeSlotID, int timeSlotLength);
    void updateTicketState(int ticketID, TicketState state);
    List<LineUpDigitalTicket> retrieveLineUpTicketsRegCustomer(int userID);
    List<BookingDigitalTicket> retrieveBookingTicketsRegCustomer(int userID);
    List<LineUpDigitalTicket> retrieveTicketsUnregisteredCustomer(int userID);
    List<LineUpDigitalTicket> retrieveDigitalTicketsStoreManager(int userID);
}
