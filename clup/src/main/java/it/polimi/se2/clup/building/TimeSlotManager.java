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

    public boolean checkTicketAvailability(int buildingId, LocalDate date, List<Integer> timeSlots, List<Department> departments){
        Map<Department, List<Integer>> availableTimeSlots = getAvailableTimeSlots(buildingId,date,Duration.ofMinutes(timeSlots.size() * 15 ),departments);
        for(Department department : departments){
            for(Integer requestedSlot : timeSlots){
                if(!availableTimeSlots.get(department).contains(requestedSlot))
                    return false;
            }
        }
        return true;
    }

    public Map<Department, List<Integer>> getAvailableTimeSlots(int buildingId, LocalDate date, Duration permanenceTime, List<Department> departments) {
        Building building = dataAccess.retrieveBuilding(buildingId);

        if (building == null) return null;

        Map<Department, List<Integer>> bookedTimeSlots= dataAccess.retrieveTimeSlots(buildingId, date);

        return computeAvailableTimeSlots(building,bookedTimeSlots,departments, permanenceTime);

    }

    //compute available time slot of each department
    private Map<Department,List<Integer>> computeAvailableTimeSlots(Building building, Map<Department,List<Integer>> bookedTimeSlots, List<Department> departments,Duration permanenceTime){
        Map<Department,List<Integer>> availableTimeSlots = new HashMap<>();

        for(Department d : departments){

            List<Integer> bookedForDepartment = bookedTimeSlots.get(d);
            List<Integer> freeTimeSlots = stripTimeSlots(building);

            //compute number book for a specific time slot.
            //key time slot identifier (number)
            //value time slot cardinality
            Map<Integer,Integer> occurrencesOfTimeSlots = computeOccurrences(bookedForDepartment);

            //remove from freetimeslots only the one that are == max for that dept.
            removeTimeSlotsOfFullDepartment(occurrencesOfTimeSlots,freeTimeSlots,d.getSurplusCapacity());

            removeSmallerThanPermanence(permanenceTime,freeTimeSlots);
            availableTimeSlots.put(d,freeTimeSlots);
        }

        return availableTimeSlots;
    }

    private void removeTimeSlotsOfFullDepartment(Map<Integer, Integer> occurrencesOfTimeSlots, List<Integer> freeTimeSlots, int surplusCapacity) {
        for(Integer timeSlot : occurrencesOfTimeSlots.keySet()){
            if(occurrencesOfTimeSlots.get(timeSlot) >= surplusCapacity)
                freeTimeSlots.remove(timeSlot);
        }
    }

    private Map<Integer, Integer> computeOccurrences(List<Integer> bookedForDepartment) {
        Map<Integer,Integer> occurrencesOfTimeSlots = new HashMap<>();

        for(Integer slot : bookedForDepartment){
            if(occurrencesOfTimeSlots.containsKey(slot))
                occurrencesOfTimeSlots.replace(slot,occurrencesOfTimeSlots.get(slot)+1);
            else
                occurrencesOfTimeSlots.put(slot,1);
        }

        return occurrencesOfTimeSlots;
    }

    //remove all time slots that do not respect opening and closing time of Buildings

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


    //remove all time slots that do not have a sequence of duration equal to the required one

    private List<Integer> removeSmallerThanPermanence(Duration permanenceTime, List<Integer> timeSlots){
        int requiredSlots = (int) (permanenceTime.toMinutes() / 15);

        List<Integer> toBeRemoved = new ArrayList<>();

        for(int i=0; i < timeSlots.size(); i++){
            int start = timeSlots.get(i);
            boolean canPick = true;
            for(int j = i+1 ,x=1; j < i + requiredSlots && j < timeSlots.size() ; j++,x++){
                if (!timeSlots.get(j).equals(start + x)) {
                    canPick = false;
                    break;
                }
            }

            if(!canPick)
                toBeRemoved.add(start);
        }

        timeSlots.removeAll(toBeRemoved);
        return timeSlots;
    }

    public BuildingDataAccess getDataAccess() {
        return dataAccess;
    }

    public void setDataAccess(BuildingDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
}
