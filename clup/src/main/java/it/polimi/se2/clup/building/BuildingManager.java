package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.BuildingDataAccess;
import it.polimi.se2.clup.data.TicketManager;
import it.polimi.se2.clup.data.entities.BookingDigitalTicket;
import it.polimi.se2.clup.data.entities.Building;
import it.polimi.se2.clup.data.entities.Department;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;

import javax.ejb.Local;
import javax.persistence.EntityExistsException;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BuildingManager implements BuildingManagerInterface{
    BuildingDataAccess dataAccess = new BuildingDataAccess();

    @Override
    public List<Integer> getAvailableTimeSlots(int buildingId, Date date, Duration permanenceTime, List<Department> departments) {
        return null;
    }

    @Override
    public boolean checkTicketAvailability(int buildingId, List<Integer> timeSlots, List<Department> departments) {
        return false;
    }

    @Override
    public boolean customerExit(int buildingId) {
        LocalDateTime lastExitTime = LocalDateTime.ofInstant(Instant.now(),ZoneId.systemDefault());

        dataAccess.updateStatistics(buildingId, lastExitTime);

        dataAccess.updateLastExitTime(buildingId, lastExitTime); //can be included in updateStatistics

        List<LineUpDigitalTicket> queue = dataAccess.retrieveTicketInQueue(buildingId);
        LineUpDigitalTicket ticket;
        if (queue.size() > 0) {
            ticket = queue.get(0);

            //TODO: Remove
            return true;
            //return ticketManager.validateTicket(ticket.getTicketID()); //should remove from queue
        }else
            return false;

    }

    @Override
    public boolean insertBuilding(String name, LocalTime opening, LocalTime closing, String address, int capacity, Map<String, Integer> surplus) {

        if(name == null || opening == null || closing == null || address == null || capacity > 0)
            return false;

        dataAccess.insertBuilding(name,opening,closing,address, capacity ,surplus,generateAccessCode());

        return true;

    }

    @Override
    public boolean insertInQueue(LineUpDigitalTicket ticket){

        if(ticket.getBuilding() == null)
            throw new IllegalArgumentException("LineUpDigitalTicket's Building not found");

        dataAccess.insertInQueue(ticket);

        return true;
    }

    private String generateAccessCode(){
        //TODO: secureRandom

        for (Building b : dataAccess.retrieveBuildings()){

        }

        return null;
    }

}
