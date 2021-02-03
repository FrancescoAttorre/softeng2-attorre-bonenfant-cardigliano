package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.BuildingDataAccess;
import it.polimi.se2.clup.data.BuildingDataAccessInterface;
import it.polimi.se2.clup.data.entities.Building;
import it.polimi.se2.clup.data.entities.Department;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;
import it.polimi.se2.clup.ticket.TicketManager;

import javax.ejb.EJB;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BuildingManager implements BuildingManagerInterface{
    @EJB
    protected BuildingDataAccessInterface dataAccess;

    //TODO : EJB also for internal ???

    @EJB
    protected QueueManager queueManager;
    @EJB
    protected TimeSlotManager timeSlotManager;
    @EJB
    protected TicketManager ticketManager;


    @Override
    public Map<Department, List<Integer>> getAvailableTimeSlots(int buildingId, LocalDate date, Duration permanenceTime, List<Department> departments) {

        return timeSlotManager.getAvailableTimeSlots(buildingId, date, permanenceTime, departments);

    }

    @Override
    public boolean checkTicketAvailability(int buildingId, List<Integer> timeSlots, List<Department> departments) {
        return false;
    }

    @Override
    public boolean customerExit(int buildingId) {
        LocalTime lastExitTime = LocalTime.ofInstant(Instant.now(),ZoneId.systemDefault());

        dataAccess.updateStatistics(buildingId, lastExitTime);

        dataAccess.updateLastExitTime(buildingId, lastExitTime); //can be included in updateStatistics

        LineUpDigitalTicket ticket = queueManager.getNext(buildingId);

        if(ticket != null) {
            ticketManager.validateTicket(ticket.getTicketID());
            queueManager.removeFromQueue(ticket.getTicketID());
            return true;
        }else
            return false;

    }

    @Override
    public boolean insertBuilding(String name, LocalTime opening, LocalTime closing, String address, int capacity, Map<String, Integer> surplus, String code) {

        //TODO : add activity connection

        if (name == null || opening == null || closing == null || address == null || capacity <= 0)
            return false;

        if (isAccessCodeAvailability(code)){
            dataAccess.insertBuilding(name, opening, closing, address, capacity, surplus, code);
            return true;
        } else
            return false;
    }

    @Override
    public boolean insertInQueue(LineUpDigitalTicket ticket){
        return queueManager.insertInQueue(ticket);
    }

    private boolean isAccessCodeAvailability(String code){
        //TODO: should check the hashed ?
        
        List<String> accessCodes = new ArrayList<>();

        for (Building b : dataAccess.retrieveBuildings()){
            accessCodes.add(b.getAccessCode());
        }

        return !accessCodes.contains(code);

    }


    public QueueManager getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    public TimeSlotManager getTimeSlotManager() {
        return timeSlotManager;
    }

    public void setTimeSlotManager(TimeSlotManager timeSlotManager) {
        this.timeSlotManager = timeSlotManager;
    }

    public TicketManager getTicketManager() {
        return ticketManager;
    }

    public void setTicketManager(TicketManager ticketManager) {
        this.ticketManager = ticketManager;
    }

    public BuildingDataAccessInterface getDataAccess() {
        return dataAccess;
    }

    public void setDataAccess(BuildingDataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }
}
