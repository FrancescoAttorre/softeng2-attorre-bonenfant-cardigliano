package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.BuildingDataAccess;
import it.polimi.se2.clup.data.entities.Building;
import it.polimi.se2.clup.data.entities.Department;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;

import java.time.*;
import java.util.List;
import java.util.Map;

public class BuildingManager implements BuildingManagerInterface{
    BuildingDataAccess dataAccess = new BuildingDataAccess();

    QueueManager queueManager = new QueueManager();
    TimeSlotManager timeSlotManager = new TimeSlotManager();

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

        if(ticket != null)
            return true;
            //TODO: remove //
            //return ticketManager.validateTicket(ticket.getTicketID());
        else
            return false;

    }

    @Override
    public boolean insertBuilding(String name, LocalTime opening, LocalTime closing, String address, int capacity, Map<String, Integer> surplus) {

        if(name == null || opening == null || closing == null || address == null || capacity > 0)
            return false;

        dataAccess.insertBuilding(name,opening,closing,address, capacity ,surplus,AccessCodeManager.generateAccessCode());

        return true;

    }

    @Override
    public boolean insertInQueue(LineUpDigitalTicket ticket){
        return queueManager.insertInQueue(ticket);
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(QueueManager queueManager) {
        this.queueManager = queueManager;
    }
}
