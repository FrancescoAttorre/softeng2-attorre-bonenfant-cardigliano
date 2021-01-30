package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.Building;
import it.polimi.se2.clup.data.entities.Department;
import it.polimi.se2.clup.data.entities.DigitalTicket;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface BuildingDataAccessInterface {
    List<Building> retrieveBuildings();
    void insertBuilding(String name, LocalTime opening, LocalTime closing, String address, int capacity, Map<String,Integer> surplus, String accessCode);
    boolean retrieveBuildingState(int id);
    boolean insertInQueue(LineUpDigitalTicket ticket);
    List<Integer> retrieveTimeSlots(int buildingId, Date date);
    List<LineUpDigitalTicket> retrieveTicketInQueue(int buildingId);
    boolean updateStatistics(int buildingId, LocalDateTime lastExitTime);
    boolean updateLastExitTime(int buildingId, LocalDateTime lastExitTime);
}
