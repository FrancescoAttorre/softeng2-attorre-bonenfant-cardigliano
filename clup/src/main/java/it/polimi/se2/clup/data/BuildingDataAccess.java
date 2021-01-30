package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.*;
import it.polimi.se2.clup.data.entities.Queue;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.*;

public class BuildingDataAccess implements BuildingDataAccessInterface{

    @PersistenceContext(unitName = "clup")
    protected EntityManager em;

    @Override
    public List<Building> retrieveBuildings() {
        return em.createNamedQuery("Building.findAll", Building.class).getResultList();
    }

    @Override
    public boolean retrieveBuildingState(int id) throws NoResultException,NonUniqueResultException{
        Building building =  em.createNamedQuery("Building.retrieveBuildingById", Building.class)
                .setParameter("buildingId",id)
                .getSingleResult();

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
    public List<Integer> retrieveTimeSlots(int buildingId, Date date) {
        List<BookingDigitalTicket> tickets = em.createNamedQuery("BookingDigitalTicket.selectByBuildingIdAndDate",BookingDigitalTicket.class)
                .setParameter("date" , date).setParameter("buildingId",buildingId).getResultList();

        List<Integer> timeSlots = new ArrayList<>();

        for (BookingDigitalTicket b: tickets ){
            for(int i = 0; i < b.getTimeSlotLength(); i++){
                timeSlots.add(b.getTimeSlotID() + i);
            }
        }

        return  timeSlots;
    }

    @Override
    public void insertBuilding(String name, LocalTime opening, LocalTime closing, String address, int capacity, Map<String, Integer> surplus,String accessCode) {
        Building building = new Building();
        building.setName(name);
        building.setOpening(opening);
        building.setClosing(closing);
        building.setAddress(address);
        building.setCapacity(capacity);

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
    public boolean updateStatistics(int buildingId, LocalDateTime lastExitTime) {
        Building building = em.find(Building.class, buildingId);

        if(building == null) return false;

        //simple version
        //weight 0.8 for old delta

        double weightedOldDelta = building.getDeltaExitTime() * 0.8 ;
        double weightedNewDelta = (double)lastExitTime.get(ChronoField.SECOND_OF_DAY) - (double)building.getLastExitTime() * 0.2;

        //TODO : check order of new delta

        building.setDeltaExitTime((int)(weightedNewDelta + weightedOldDelta));

        return true;
    }

    @Override
    public boolean updateLastExitTime(int buildingId, LocalDateTime lastExit) {
        Building building = em.find(Building.class,buildingId);

        if(building == null) return false;

        building.setLastExitTime(lastExit.get(ChronoField.SECOND_OF_DAY)); //should throw an exception ?
        return true;
    }
}