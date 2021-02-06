package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.entities.Building;
import it.polimi.se2.clup.data.entities.Department;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TimeSlotManagerInterface {

    boolean checkTicketAvailability(int buildingId, LocalDate date, List<Integer> timeSlots, List<Department> departments);

    Map<Department, List<Integer>> getAvailableTimeSlots(int buildingId, LocalDate date, Duration permanenceTime, List<Department> departments);
}
