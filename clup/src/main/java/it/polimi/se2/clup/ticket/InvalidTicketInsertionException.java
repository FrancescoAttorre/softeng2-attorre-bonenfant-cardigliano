package it.polimi.se2.clup.ticket;

public class InvalidTicketInsertionException extends Exception {
    public InvalidTicketInsertionException() {super("This customer has already acquired a ticket for that building");}
}
