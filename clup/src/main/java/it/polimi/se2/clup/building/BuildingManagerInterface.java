package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.entities.Department;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface BuildingManagerInterface {

    //List<Building> getAvailableBuildings(Location position, String meansOfTransport);
    Map<Department, List<Integer>> getAvailableTimeSlots(int buildingId, LocalDate date, Duration permanenceTime, List<Department> departments);
    boolean checkTicketAvailability(int buildingId, LocalDate date, List<Integer> timeSlots, List<Department> departments);
    boolean customerExit(int buildingId);
    boolean insertBuilding(int activityId, String name, LocalTime opening, LocalTime closing, String address, int capacity, Map<String,Integer> surplus,String code);
    boolean insertInQueue(LineUpDigitalTicket ticket);
    boolean customerEntry (int ticketID, int buildingID, int userID);
    void reduceCapacity(int buildingID);
    boolean checkBuildingNotFull (int buildingID);
}
