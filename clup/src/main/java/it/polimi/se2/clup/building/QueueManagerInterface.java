package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;

public interface QueueManagerInterface {


    boolean insertInQueue(LineUpDigitalTicket ticket);

    LineUpDigitalTicket getNext(int buildingId);

    void removeFromQueue(int ticketId);
}
