package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.BuildingDataAccess;
import it.polimi.se2.clup.data.BuildingDataAccessInterface;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class QueueManager implements QueueManagerInterface{

    @EJB
    BuildingDataAccessInterface dataAccess;

    @Override
    public boolean insertInQueue(LineUpDigitalTicket ticket){
        if(ticket.getBuilding() == null)
            throw new IllegalArgumentException("LineUpDigitalTicket's Building not found");

        dataAccess.insertInQueue(ticket);

        return true;
    }

    @Override
    public LineUpDigitalTicket getNext(int buildingId){
        List<LineUpDigitalTicket> queue = dataAccess.retrieveTicketsInQueue(buildingId);
        LineUpDigitalTicket ticket;
        if (queue.size() > 0) {
            ticket = queue.get(0);
            return ticket;
        }else
            return null;
    }

    @Override
    public void removeFromQueue(int ticketId){
        dataAccess.removeFromQueue(ticketId);
    }

    public void setDataAccess(BuildingDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
}
