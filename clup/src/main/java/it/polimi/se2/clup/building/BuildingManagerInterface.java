package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.entities.*;
import it.polimi.se2.clup.externalServices.Position;

import javax.ejb.Local;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
@Local
public interface BuildingManagerInterface {

    List<Building> retrieveBuilding(int activityId);
    List<Building> getAvailableBuildings(Position position, MeansOfTransport meansOfTransport);
    Map<Department, List<Integer>> getAvailableTimeSlots(int buildingId, LocalDate date, Duration permanenceTime, List<Department> departments);
    boolean checkTicketAvailability(int buildingId, LocalDate date, List<Integer> timeSlots, List<Department> departments);
    void customerExit(int buildingId, int ticketID);
    void registeredCustomerExit(int buildingId, BookingDigitalTicket bookingDigitalTicket);
    boolean insertBuilding(int activityId, String name, LocalTime opening, LocalTime closing, String address, int capacity, Map<String,Integer> surplus,String code);
    boolean customerEntry (int ticketID, int buildingID, int userID, List<BookingDigitalTicket> bookingDigitalTickets);
    void reduceCapacity(int buildingID);
    boolean checkBuildingNotFull (int buildingID);
    boolean insertInQueue(LineUpDigitalTicket ticket);

}
