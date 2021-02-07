package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;
import it.polimi.se2.clup.ticket.NotInQueueException;

import javax.ejb.Local;
import java.time.Duration;

/**
 * Interface implemented by BuildingManager and used by TicketManager to compute updated waiting times for a given
 * line up ticket
 */
@Local
public interface WaitingTimeInt  {

    Duration computeWaitingTime (LineUpDigitalTicket ticket) throws NotInQueueException;
}
