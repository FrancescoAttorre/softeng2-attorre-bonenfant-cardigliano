package it.polimi.se2.clup.ticket;

import it.polimi.se2.clup.data.entities.TicketState;

import javax.ejb.Local;

@Local
public interface TicketValidationInt {

    boolean validateTicket(int ticketID);
    boolean validityCheck (int ticketID);
    boolean setTicketState(int ticketID, TicketState state);
}
