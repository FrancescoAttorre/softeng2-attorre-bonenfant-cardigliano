package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.Building;
import it.polimi.se2.clup.data.entities.Department;
import it.polimi.se2.clup.data.entities.DigitalTicket;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface BuildingDataAccessInterface {
    List<Building> retrieveBuildings();
    boolean insertBuilding(int activityId, String name, LocalTime opening, LocalTime closing, String address, int capacity, Map<String,Integer> surplus, String accessCode);
    Building retrieveBuilding(int buildingId);
    Building retrieveBuilding(String name);
    boolean retrieveBuildingState(int id);
    boolean insertInQueue(LineUpDigitalTicket ticket);
    Map<Department,List<Integer>> retrieveTimeSlots(int buildingId, LocalDate date);
    List<LineUpDigitalTicket> retrieveTicketsInQueue(int buildingId);
    boolean updateStatistics(int buildingId, LocalTime lastExitTime);
    boolean updateLastExitTime(int buildingId, LocalTime lastExitTime);

    DigitalTicket retrieveTicket(int ticketID);
    void removeFromQueue(int ticketId);
}
