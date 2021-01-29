package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.DigitalTicket;
import it.polimi.se2.clup.data.entities.TicketState;
import it.polimi.se2.clup.data.entities.TimeSlot;

import java.util.Date;
import java.util.List;

public interface TicketDataAccessInterface {

    void insertCustomerLineUpTicket (int userID, int buildingID);
    void insertStoreManagerLineUpTicket (int userID);
    void insertBookingTicket(int userID, int buildingID, Date date, TimeSlot timeSlot);
    void updateTicketState(int ticketID, TicketState state);
    List<DigitalTicket> retrieveTickets (int userID);
}
