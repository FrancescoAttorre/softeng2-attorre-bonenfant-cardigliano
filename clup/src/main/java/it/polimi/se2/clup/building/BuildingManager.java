package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.BuildingDataAccessInterface;
import it.polimi.se2.clup.data.entities.*;
import it.polimi.se2.clup.ticket.TicketManager;

import javax.ejb.Stateless;
import javax.ejb.EJB;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Stateless
public class BuildingManager implements BuildingManagerInterface{
    @EJB
    protected BuildingDataAccessInterface dataAccess;

    //TODO : EJB also for internal ???

    @EJB
    protected QueueManager queueManager;
    @EJB
    protected TimeSlotManager timeSlotManager;
    @EJB
    protected TicketManager ticketManager;

    public static final int minutesInASlot = 15;
    public static final int expirationTime = 10;

    @Override
    public Map<Department, List<Integer>> getAvailableTimeSlots(int buildingId, LocalDate date, Duration permanenceTime, List<Department> departments) {

        return timeSlotManager.getAvailableTimeSlots(buildingId, date, permanenceTime, departments);

    }

    @Override
    public boolean checkTicketAvailability(int buildingId, LocalDate date, List<Integer> timeSlots, List<Department> departments) {
        return timeSlotManager.checkTicketAvailability(buildingId,date,timeSlots,departments);
    }

    @Override
    public boolean customerExit(int buildingId) {

        LocalTime lastExitTime = LocalTime.now();

        dataAccess.updateStatistics(buildingId, lastExitTime);

        dataAccess.updateLastExitTime(buildingId, lastExitTime); //can be included in updateStatistics

        LineUpDigitalTicket ticket = queueManager.getNext(buildingId);

        if(ticket != null) {
            ticketManager.validateTicket(ticket.getTicketID());
            queueManager.removeFromQueue(ticket.getTicketID());
            return true;
        }
        else {
            Building building = dataAccess.retrieveBuilding(buildingId);
            building.setActualCapacity(building.getActualCapacity() + 1);
        }
        return false; //false means that no one else is allowed to enter the building?
    }

    /**
     * Method called by StoreManager to allow a customer to enter a building or prevent him from entering
     * LineUp tickets are set to valid during exits/ticket acquisition, while booking ticket are validated in this method
     * @return true if the customer has a valid ticket
     */
    @Override
    public boolean customerEntry (int ticketID, int buildingID, int userID) {

        boolean result = false;

        List<BookingDigitalTicket> bookingTickets = ticketManager.getBookingTicketsRegCustomer(userID);
        BookingDigitalTicket ticket = null;

        if (bookingTickets != null) {
            for (BookingDigitalTicket bookingTicket : bookingTickets)
                if (bookingTicket.getTicketID() == ticketID)
                    ticket = bookingTicket;
        }

        if (ticket != null) {
            int startingMinute = ticket.getTimeSlotID() * minutesInASlot;
            int year = ticket.getDate().getYear();
            int month = ticket.getDate().getMonthValue();
            int day = ticket.getDate().getDayOfMonth();
            int hour = startingMinute / 60;
            int minute = startingMinute % 60;

            if (year == LocalDateTime.now().getYear() && month == LocalDateTime.now().getMonthValue() &&
                    day == LocalDateTime.now().getDayOfMonth() && hour == LocalTime.now().getHour() &&
                    Duration.between(LocalTime.of(hour, minute), LocalTime.now()).toMinutes() < expirationTime) {

                ticketManager.validateTicket(ticketID);
                result = true;
            }
            if (Duration.between(LocalTime.of(hour, minute), LocalTime.now()).toMinutes() > expirationTime)
                ticketManager.setTicketState(ticketID, TicketState.EXPIRED);
        }
        else if (ticketManager.validityCheck(ticketID)) {

            Building building = dataAccess.retrieveBuilding(buildingID);
            if (building == null)
                return false;
            building.setActualCapacity(building.getActualCapacity() - 1);
            result = true;
        }
        return result;
    }

    @Override
    public boolean insertBuilding(int activityId,String name, LocalTime opening, LocalTime closing, String address, int capacity, Map<String, Integer> surplus, String code) {

        //TODO : add activity connection

        if (name == null || opening == null || closing == null || address == null || capacity <= 0)
            return false;

        if (isAccessCodeAvailable(code)){
            dataAccess.insertBuilding(activityId,name, opening, closing, address, capacity, surplus, code);
            return true;
        } else
            return false;
    }

    @Override
    public boolean insertInQueue(LineUpDigitalTicket ticket){
        return queueManager.insertInQueue(ticket);
    }

    private boolean isAccessCodeAvailable(String code){
        List<String> accessCodes = new ArrayList<>();

        for (Building b : dataAccess.retrieveBuildings()){
            accessCodes.add(b.getAccessCode());
        }

        return !accessCodes.contains(code);

    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    public TimeSlotManager getTimeSlotManager() {
        return timeSlotManager;
    }

    public void setTimeSlotManager(TimeSlotManager timeSlotManager) {
        this.timeSlotManager = timeSlotManager;
    }

    public TicketManager getTicketManager() {
        return ticketManager;
    }

    public void setTicketManager(TicketManager ticketManager) {
        this.ticketManager = ticketManager;
    }

    public BuildingDataAccessInterface getDataAccess() {
        return dataAccess;
    }

    public void setDataAccess(BuildingDataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    //methods used by ticket manager

    public boolean checkBuildingNotFull (int buildingID) {
        return dataAccess.retrieveBuilding(buildingID).getActualCapacity() > 0;
    }

    public void reduceCapacity(int buildingID) {
        Building building = dataAccess.retrieveBuilding(buildingID);
        int actualCapacity = building.getActualCapacity();
        building.setActualCapacity(actualCapacity - 1);
    }

}
