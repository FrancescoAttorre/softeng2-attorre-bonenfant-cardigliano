package it.polimi.se2.clup.ticket;

import it.polimi.se2.clup.data.entities.TicketState;

import javax.ejb.Local;

/**
 * Interface implemented by TicketManager, with methods used also by BuildingManager component in order to
 * validate a ticket at the right time
 */
@Local
public interface TicketValidationInt {

    boolean validateTicket(int ticketID);
    boolean validityCheck (int ticketID);
    boolean setTicketState(int ticketID, TicketState state);
}
