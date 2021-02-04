package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.BuildingDataAccess;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;

import java.util.List;

public class QueueManager {

    BuildingDataAccess dataAccess;

    public boolean insertInQueue(LineUpDigitalTicket ticket){
        if(ticket.getBuilding() == null)
            throw new IllegalArgumentException("LineUpDigitalTicket's Building not found");

        dataAccess.insertInQueue(ticket);

        return true;
    }

    //TODO
    public LineUpDigitalTicket getNext(int buildingId){
        List<LineUpDigitalTicket> queue = dataAccess.retrieveBuilding(buildingId).getQueue().getQueueTickets();
         //dataAccess.retrieveTicketsInQueue(buildingId);
        LineUpDigitalTicket ticket;
        if (queue.size() > 0) {
            ticket = queue.get(0);
            return ticket;
        }else
            return null;
    }

    public void removeFromQueue(int ticketId){
        dataAccess.removeFromQueue(ticketId);
    }

    public BuildingDataAccess getDataAccess() {
        return dataAccess;
    }

    public void setDataAccess(BuildingDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
}
