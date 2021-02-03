package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.BuildingDataAccess;
import it.polimi.se2.clup.data.entities.Building;
import it.polimi.se2.clup.data.entities.Department;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSlotManager {
    private static final int TOTALTIMESLOT = 96;
    private static final int TIMESLOTDURATION = 15;
    BuildingDataAccess dataAccess = new BuildingDataAccess();

    public Map<Department, List<Integer>> getAvailableTimeSlots(int buildingId, LocalDate date, Duration permanenceTime, List<Department> departments) {
        Building building = dataAccess.retrieveBuilding(buildingId);

        if (building == null) return null;

        Map<Department, List<Integer>> bookedTimeSlots= dataAccess.retrieveTimeSlots(buildingId, date);

        return computeAvailableTimeSlots(building,bookedTimeSlots,departments, permanenceTime);

    }


    private Map<Department,List<Integer>> computeAvailableTimeSlots(Building building, Map<Department,List<Integer>> bookedTimeSlots, List<Department> departments,Duration permanenceTime){
        Map<Department,List<Integer>> availableTimeSlots = new HashMap<>();

        for(Department d : departments){
            List<Integer> timeslots = bookedTimeSlots.get(d);
            List<Integer> freeTimeSlots = stripTimeSlots(building);
            freeTimeSlots.removeAll(timeslots);
            availableTimeSlots.put(d,freeTimeSlots);
        }

        //for (int i = 0, i )

        return availableTimeSlots;
    }

    private List<Integer> stripTimeSlots(Building building){
        //number of TS before opening
        int beforeTS =  building.getOpening().get(ChronoField.MINUTE_OF_DAY) / TIMESLOTDURATION;
        //number of TS up to closing
        int upToClosingTS = building.getClosing().get(ChronoField.MINUTE_OF_DAY) / TIMESLOTDURATION;

        List<Integer> strippedTimeSlots = new ArrayList<>();
        for(int i = beforeTS; i < upToClosingTS; i++){
            strippedTimeSlots.add(i);
        }
        return strippedTimeSlots;
    }

    public BuildingDataAccess getDataAccess() {
        return dataAccess;
    }

    public void setDataAccess(BuildingDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
}
