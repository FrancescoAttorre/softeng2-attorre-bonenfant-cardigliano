package it.polimi.se2.clup.ticket;

import it.polimi.se2.clup.data.entities.*;

import javax.ejb.Local;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interface implemented by TicketManager with methods related to managing tickets, acquiring a new one, obtaining existing ones,
 * changing their status, validate them.
 */
@Local
public interface TicketManagerInterface {

    LineUpDigitalTicket acquireStoreManagerTicket(int userID, int buildingID, boolean buildingIsFull);

    boolean acquireBookingTicket(int userID, int buildingID, LocalDate date, int timeSlotID, int timeSlotLength, List<Integer> departments) throws Exception;

    LineUpDigitalTicket acquireUnregCustomerLineUpTicket(int userID, int buildingID, boolean buildingIsFull) throws InvalidTicketInsertionException;

    LineUpDigitalTicket acquireRegCustomerLineUpTicket(int userID, int buildingID, boolean buildingIsFull) throws InvalidTicketInsertionException;

    List<BookingDigitalTicket> getBookingTicketsRegCustomer(int userID);
    List<LineUpDigitalTicket> getLineUpTicketsRegCustomer (int userID);
    List<LineUpDigitalTicket> getTicketsUnregisteredCustomer (int userID);
    List<LineUpDigitalTicket> getLineUpTicketsStoreManager (int userID);

    Map<LineUpDigitalTicket, Duration> getWaitingUpdateRegCustomer(int userID) throws NotInQueueException;
    Map<LineUpDigitalTicket, Duration> getWaitingUpdateSM(int userID) throws NotInQueueException;
    Map<LineUpDigitalTicket, Duration> getWaitingUpdateUnregCustomer(int userID) throws NotInQueueException;
}
