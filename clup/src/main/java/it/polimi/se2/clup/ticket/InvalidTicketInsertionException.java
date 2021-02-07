package it.polimi.se2.clup.ticket;

/**
 * Exception thrown if a customer can't acquire a ticket for the given building
 */
public class InvalidTicketInsertionException extends Exception {
    public InvalidTicketInsertionException() {super("This customer has already acquired a ticket for the given building");}
}
