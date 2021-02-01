package it.polimi.se2.clup.ticket;

public class NotInQueueException extends Exception {
    public NotInQueueException() { super("Ticket not found in queue"); }
}