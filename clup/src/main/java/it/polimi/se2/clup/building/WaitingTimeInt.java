package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;
import it.polimi.se2.clup.ticket.NotInQueueException;

import javax.ejb.Local;
import java.time.Duration;

@Local
public interface WaitingTimeInt  {

    Duration computeWaitingTime (LineUpDigitalTicket ticket) throws NotInQueueException;
}
