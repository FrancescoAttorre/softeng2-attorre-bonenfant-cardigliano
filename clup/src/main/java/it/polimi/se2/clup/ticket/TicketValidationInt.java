package it.polimi.se2.clup.ticket;

import javax.ejb.Local;

@Local
public interface TicketValidationInt {

    boolean validateTicket(int ticketID);
    boolean validityCheck (int ticketID);
}
