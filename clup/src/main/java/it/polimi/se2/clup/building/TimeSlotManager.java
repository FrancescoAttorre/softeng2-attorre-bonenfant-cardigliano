package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.BuildingDataAccess;
import it.polimi.se2.clup.data.entities.Building;
import it.polimi.se2.clup.data.entities.Department;

import javax.ejb.Stateless;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class TimeSlotManager implements TimeSlotManagerInterface{
    private static final int TOTALTIMESLOT = 96;
    private static final int TIMESLOTDURATION = 15;
    BuildingDataAccess dataAccess = new BuildingDataAccess();


    @Override
    public boolean checkTicketAvailability(int buildingId, LocalDate date, List<Integer> timeSlots, List<Department> departments){
        Map<Department, List<Integer>> availableTimeSlots = getAvailableTimeSlots(buildingId,date,Duration.ofMinutes((long) timeSlots.size() * TIMESLOTDURATION ),departments);

        for(Department department : departments){

            if(!availableTimeSlots.containsKey(department)) return false;

            for(Integer requestedSlot : timeSlots){
                if(!availableTimeSlots.get(department).contains(requestedSlot))
                    return false;
            }

        }

        return true;
    }
    
    @Override
    public Map<Department, List<Integer>> getAvailableTimeSlots(int buildingId, LocalDate date, Duration permanenceTime, List<Department> departments) {
        Building building = dataAccess.retrieveBuilding(buildingId);

        if (building == null) return null;

        Map<Department, List<Integer>> bookedTimeSlots = dataAccess.retrieveTimeSlots(buildingId, date);

        return computeAvailableTimeSlots(building,bookedTimeSlots,departments, permanenceTime);

    }


    /**
     * Utility method able to compute available time slot for each department using other utility method of the manager.
     * @param bookedTimeSlots List of booked time slot for a specific building and date
     * @param departments List of departments chosen for which a ticket want to be acquired
     * @param permanenceTime Duration of minutes for which a ticket want to be acquired
     * @return a Map with key Department and value a List of the available time slots
     */
    public Map<Department,List<Integer>> computeAvailableTimeSlots(Building building, Map<Department, List<Integer>> bookedTimeSlots, List<Department> departments, Duration permanenceTime){
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

    /**
     * Utility method able to remove all time slots that will exceed surplus of departments if picked.
     */
    private void removeTimeSlotsOfFullDepartment(Map<Integer, Integer> occurrencesOfTimeSlots, List<Integer> freeTimeSlots, int surplusCapacity) {
        for(Integer timeSlot : occurrencesOfTimeSlots.keySet()){
            if(occurrencesOfTimeSlots.get(timeSlot) >= surplusCapacity)
                freeTimeSlots.remove(timeSlot);
        }
    }

    /**
     * Utility method able to compute number of book for a specific time slot.
     * Surplus capacity should be respected for each departments and time slot
     * @param bookedForDepartment List of time slots booked for a department
     * @return a Map of time slot identifier and occurrences of that time slot
     */
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

    /**
     * Utility method able to remove all time slot that do not respect opening and closing time of building
     * @return List of TimeSlots, without closed hours timeSlots
     */
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



    /**
     * Utility method able to remove all time slot that do not respect the required duration.
     * a sequence of time slot of a required length should be present in the list returned
     * @param permanenceTime Duration in minutes of the visit
     * @param timeSlots TimeSlot that wants to be cleared
     */
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
