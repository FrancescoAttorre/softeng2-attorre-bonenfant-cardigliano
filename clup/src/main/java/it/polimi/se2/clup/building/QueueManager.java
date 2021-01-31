package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.BuildingDataAccess;
import it.polimi.se2.clup.data.entities.Building;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;

import java.util.List;

public class QueueManager {

    BuildingDataAccess dataAccess = new BuildingDataAccess();

    public boolean insertInQueue(LineUpDigitalTicket ticket){
        if(ticket.getBuilding() == null)
            throw new IllegalArgumentException("LineUpDigitalTicket's Building not found");

        dataAccess.insertInQueue(ticket);

        return true;
    }

    public LineUpDigitalTicket getNext(int buildingId){
        List<LineUpDigitalTicket> queue = dataAccess.retrieveTicketInQueue(buildingId);
        LineUpDigitalTicket ticket;
        if (queue.size() > 0) {
            ticket = queue.get(0);
            return ticket;
        }else
            return null;
    }

}
