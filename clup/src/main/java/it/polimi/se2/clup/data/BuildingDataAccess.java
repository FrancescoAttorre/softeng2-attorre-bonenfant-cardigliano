package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.*;
import it.polimi.se2.clup.data.entities.Queue;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.*;

public class BuildingDataAccess implements BuildingDataAccessInterface{

    @PersistenceContext(unitName = "clup")
    protected EntityManager em;

    @Override
    public Building retrieveBuilding(int buildingId){
        //exception if null ???
        return em.find(Building.class,buildingId);
    }

    @Override
    public List<Building> retrieveBuildings() {
        return em.createNamedQuery("Building.findAll", Building.class).getResultList();
    }

    @Override
    public boolean retrieveBuildingState(int id) throws NoResultException,NonUniqueResultException{
        Building building = em.find(Building.class, id);

        return (building.getCapacity() - building.getActualCapacity()) > 0;
    }

    @Override
    public boolean insertInQueue(LineUpDigitalTicket ticket){
        int buildingId = ticket.getBuilding().getBuildingID();

        Queue queue = em.find(Queue.class, buildingId);

        if(queue == null) return false;

        queue.addQueueTickets(ticket);
        return true;
    }

    @Override
    public Map<Department,List<Integer>> retrieveTimeSlots(int buildingId, LocalDate date) {
        List<BookingDigitalTicket> tickets = em.createNamedQuery("BookingDigitalTicket.selectByBuildingIdAndDate",BookingDigitalTicket.class)
                .setParameter("date" , date).setParameter("buildingId",buildingId).getResultList();

        Building building = em.find(Building.class, buildingId);

        Map<Department,List<Integer>> bookedTimeSlots = new HashMap<>();

        for(Department d : building.getDepartments()){
            List<Integer> timeSlots = new ArrayList<>();
            for (BookingDigitalTicket b: d.getTickets() ){
                for(int i = 0; i < b.getTimeSlotLength(); i++){
                    timeSlots.add(b.getTimeSlotID() + i);
                }
            }
            bookedTimeSlots.put(d,timeSlots);
        }

        return  bookedTimeSlots;
    }

    @Override
    public void insertBuilding(String name, LocalTime opening, LocalTime closing, String address, int capacity, Map<String, Integer> surplus,String accessCode) {
        Building building = new Building();
        Queue queue = new Queue();
        queue.setBuilding(building);

        em.persist(queue);

        building.setName(name); //TODO: name should be unique ? check or catch exception ?
        building.setOpening(opening);
        building.setClosing(closing);
        building.setAddress(address);
        building.setCapacity(capacity);
        building.setAccessCode(accessCode);
        building.setQueue(queue);
        building.setDeltaExitTime(Duration.ZERO);

        for (String deptName : surplus.keySet()){
            Department department = new Department();
            department.setName(deptName);
            department.setSurplusCapacity(surplus.get(deptName));

            building.addDepartment(department);
        }

        em.persist(building);
    }

    @Override
    public List<LineUpDigitalTicket> retrieveTicketInQueue(int buildingId) {
        return em.createNamedQuery("Queue.selectTicketInQueueByBuildingId", LineUpDigitalTicket.class)
                .setParameter("buildingId",buildingId)
                .getResultList();
    }

    @Override
    public boolean updateStatistics(int buildingId, LocalTime lastExitTime) {
        Building building = em.find(Building.class, buildingId);

        if(building == null) return false;

        //simple version
        //weight 0.8 for old delta
        double weightedOldDelta;
        double weightedNewDelta;
        if(building.getDeltaExitTime().equals(Duration.ZERO)){
            weightedNewDelta = (double)lastExitTime.get(ChronoField.SECOND_OF_DAY) - (double)building.getOpening().get(ChronoField.SECOND_OF_DAY);

            building.setDeltaExitTime(Duration.ofSeconds((long) weightedNewDelta));
        }else {
            weightedOldDelta = building.getDeltaExitTime().toSeconds() * 0.8 ;
            weightedNewDelta = (double)lastExitTime.get(ChronoField.SECOND_OF_DAY) - (double)building.getLastExitTime() * 0.2;

            //TODO : check order of new delta
            building.setDeltaExitTime(Duration.ofSeconds((long)(weightedNewDelta + weightedOldDelta)));
        }

        return true;
    }

    @Override
    public boolean updateLastExitTime(int buildingId, LocalTime lastExit) {
        Building building = em.find(Building.class,buildingId);

        if(building == null) return false;

        building.setLastExitTime(lastExit.get(ChronoField.SECOND_OF_DAY)); //should throw an exception ?
        return true;
    }
}