package it.polimi.se2.clup.ticket;

/**
 * Exception thrown when a ticket is not found in the queue where it should be
 */
public class NotInQueueException extends Exception {

    public NotInQueueException() { super("Ticket not found in queue"); }

}