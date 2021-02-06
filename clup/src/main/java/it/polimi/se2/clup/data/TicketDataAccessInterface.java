package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.BookingDigitalTicket;
import it.polimi.se2.clup.data.entities.Department;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;
import it.polimi.se2.clup.data.entities.TicketState;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public interface TicketDataAccessInterface {

    LineUpDigitalTicket insertUnregCustomerLineUpTicket(int userID, int buildingID);
    LineUpDigitalTicket insertRegCustomerLineUpTicket (int userID, int buildingID);
    LineUpDigitalTicket insertStoreManagerLineUpTicket (int userID);
    BookingDigitalTicket insertBookingTicket(int userID, int buildingID, LocalDate date, int timeSlotID, int timeSlotLength,
                                             List<Department> chosenDepartments) throws Exception;
    boolean updateTicketState(int ticketID, TicketState state);
    List<LineUpDigitalTicket> retrieveLineUpTicketsRegCustomer(int userID);
    List<BookingDigitalTicket> retrieveBookingTicketsRegCustomer(int userID);
    List<LineUpDigitalTicket> retrieveTicketsUnregisteredCustomer(int userID);
    List<LineUpDigitalTicket> retrieveLineUpTicketsStoreManager(int userID);

    List<LineUpDigitalTicket> retrieveAllLineUpTickets(int buildingID);
    LocalDateTime retrieveAcquisitionTime (LineUpDigitalTicket lineUpTicket);
    LocalDateTime retrieveValidationTime (int ticketID);
    TicketState retrieveTicketState (int ticketID);
    int retrieveCapacity (int buildingID);
}
