package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.BuildingDataAccessInterface;
import it.polimi.se2.clup.data.entities.*;
import it.polimi.se2.clup.externalServices.MapsServiceServerAdapter;
import it.polimi.se2.clup.externalServices.Position;
import it.polimi.se2.clup.ticket.NotInQueueException;
import it.polimi.se2.clup.ticket.TicketValidationInt;

import javax.ejb.Stateless;
import javax.ejb.EJB;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class BuildingManager implements BuildingManagerInterface, WaitingTimeInt {

    @EJB
    protected BuildingDataAccessInterface dataAccess;
    @EJB
    protected QueueManagerInterface queueManager;
    @EJB
    protected TimeSlotManagerInterface timeSlotManager;
    @EJB
    protected TicketValidationInt ticketManager;
    @EJB
    protected MapsServiceServerAdapter mapsAdapter;


    public static final int minutesInASlot = 15;
    public static final int expirationTime = 10;
    public static final int defaultWaitingTime = 15;
    public static final int extraTime = 10;
    public static final Duration maxDistance = Duration.of(20, ChronoUnit.MINUTES);
    public static final double percentage = 0.4;

    /**
     * Get time slots available for each department.
     * TimeSlotManager would proceed to compute available time slots for selected departments with duration equal to permanence time.
     * should not be called if departments are not selected, because in the latter case the availability of a ticket
     * is given by the percentage of already booked ticket for the same time slot
     * @param permanenceTime minutes Duration
     * @param departments departments which want to be booked, null if there is no preference on departments
     * @return Map of time slots for each department, null if cannot look for the slots
     */
    @Override
    public Map<Department, List<Integer>> getAvailableTimeSlots(int buildingId, LocalDate date, Duration permanenceTime, List<Department> departments) {
        if( date == null || permanenceTime == null) return null;

        if(departments != null)
            return timeSlotManager.getAvailableTimeSlots(buildingId, date, permanenceTime, departments);
        else
            //return null because should not be requested time slots for a ticket with no departments choosen
            return null;


    }

    /**
     * Check if a ticket can be acquired with specified parameters
     * A ticket with chosen departments is available if selected timeSlots are available in each departments, this computation is executed by building manager
     * A ticket with not provided departments will occupy a spot in Building capacity (for at most a maximum percentage)
     * The number of booking tickets with time slot occupied equals to time slot required must be less than or equal the maximum percentage * building capacity
     * if this not happens means that exist a time slot for which the capacity is surmounted.
     * @param timeSlots List of chosen timeSlots
     * @param departments List of Departments for which time slots want to be acquired, null if no preferences is provided
     * @return true if can be acquired a ticket with provided parameters, else false
     */
    @Override
    public boolean checkTicketAvailability(int buildingId, LocalDate date, List<Integer> timeSlots, List<Department> departments) {
        if( date == null || timeSlots == null) return false; //malformed

        if(departments != null)

            //controllo sui dept che appartengano tutti

            return timeSlotManager.checkTicketAvailability(buildingId,date,timeSlots,departments);
        else
            if(date.getDayOfMonth() == LocalDate.now().getDayOfMonth() && date.getMonth() == LocalDate.now().getMonth()){
                return false;   //cannot book for same day
            }else{
                //should check if 40 percent of capacity is already booked

                Building building = dataAccess.retrieveBuilding(buildingId);

                List<DigitalTicket> tickets = building.getTickets();

                tickets.removeIf(t -> t.getClass() != LineUpDigitalTicket.class);

                boolean isAvailable = true;
                for(Integer slot : timeSlots){
                    int occurrence = 0;
                    for(DigitalTicket ticket : tickets){
                        BookingDigitalTicket bookingDigitalTicket = (BookingDigitalTicket)ticket;
                        int startingSlot = bookingDigitalTicket.getTimeSlotID();
                        int lastSlot = startingSlot + bookingDigitalTicket.getTimeSlotLength() - 1;

                        if(slot >= startingSlot || slot <= lastSlot) occurrence++;

                        if(occurrence > percentage*building.getCapacity()){
                            isAvailable = false;
                            break;
                        }
                    }
                }

                return isAvailable;

            }

    }

    /**
     * To be called when a customer exit a building.
     * Building statistics are updated, in order to compute a more precise waiting time for next request.
     * A free spot is for sure available in the Building, so the next ticket in Queue (if present) can be validated.
     * with help of utility method the new ticket is removed from queue and validated to let the User enter.
     */
    @Override
    public void customerExit(int buildingId, int ticketID) {

        LocalTime lastExitTime = LocalTime.now();

        dataAccess.updateStatistics(buildingId, lastExitTime);

        dataAccess.updateLastExitTime(buildingId, lastExitTime); //can be included in updateStatistics

        validateNext(buildingId);

        if (!dataAccess.retrieveTicket(ticketID).getState().equals(TicketState.EXPIRED))
            ticketManager.setTicketState(ticketID, TicketState.EXPIRED);

    }

    /**
     * Method called by StoreManager to allow a customer to enter a building or prevent him from entering
     * LineUp tickets are set to valid during exits/ticket acquisition, while booking ticket are validated in this method
     * @return true if the customer has a valid ticket
     */
    @Override
    public boolean customerEntry (int ticketID, int buildingID, int userID, List<BookingDigitalTicket> bookingTickets) {

        boolean result = false;

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
                //if (ticket.getDepartments().isEmpty())
                //    reduceCapacity(buildingID);
                result = true;
            }
            if (Duration.between(LocalTime.of(hour, minute), LocalTime.now()).toMinutes() > expirationTime)
                ticketManager.setTicketState(ticketID, TicketState.EXPIRED);
        }
        else if (ticketManager.validityCheck(ticketID)) {

            Building building = dataAccess.retrieveBuilding(buildingID);
            if (building == null)
                return false;

            reduceCapacity(buildingID);
            result = true;
        }
        return result;
    }

    @Override
    public boolean insertBuilding(int activityId, String name, LocalTime opening, LocalTime closing, String address, int capacity, Map<String, Integer> surplus, String code) {

        if (name == null || opening == null || closing == null || address == null || capacity <= 0)
            return false;

        if (isAccessCodeAvailable(code)){

            return dataAccess.insertBuilding(activityId, name, opening, closing, address, capacity, surplus, code);

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

    /**
     * ComputeWaitingTime sets the waiting time to zero for valid tickets,
     * changes tickets state to expired when the ticket are valid from more than 10 minutes,
     * for non valid tickets, it updates the estimated waiting time, basing on the last exit
     * from the ticket's building while in queue, the current time and the medium waiting time for that building
     *
     * In case of delays, the waiting time is fixed to the default waiting time
     *
     * @return new value of estimated waiting time with a precision of minutes
     * @throws NotInQueueException if the ticket is not found in the related queue
     */
    @Override
    public Duration computeWaitingTime (LineUpDigitalTicket ticket) throws NotInQueueException {

        Duration newWaitingTime;

        switch (ticket.getState()) {
            case EXPIRED:
                newWaitingTime = Duration.ZERO;
                break;

            case VALID:
                if (Duration.between(ticket.getValidationTime(), LocalDateTime.now()).toMinutes() > 10)
                    ticketManager.setTicketState(ticket.getTicketID(), TicketState.EXPIRED);
                newWaitingTime = Duration.ZERO;
                break;

            case INVALID:
                Duration additionalTime = Duration.ofMinutes(extraTime);
                LocalTime now = LocalTime.now();

                Queue queue = ticket.getQueue();
                if (queue == null)
                    throw  new NotInQueueException();

                int positionInQueue = ticket.getQueue().getQueueTickets().indexOf(ticket);

                if (positionInQueue != -1) {

                    long averageWait = ticket.getBuilding().getDeltaExitTime().toMinutes();
                    LocalTime lastExitTime = ticket.getBuilding().getLastExitTime();
                    LocalTime acquisitionTime = ticket.getAcquisitionTime().toLocalTime();

                    if (lastExitTime != null && lastExitTime.isAfter(acquisitionTime)) {

                        newWaitingTime = Duration.ofMinutes((averageWait * (positionInQueue + 1)) -
                                Duration.between(lastExitTime, now).toMinutes());
                    }

                    else if (!ticket.getBuilding().getDeltaExitTime().equals(Duration.ZERO)) {

                        newWaitingTime = Duration.ofMinutes(averageWait * (positionInQueue + 1) -
                                Duration.between(acquisitionTime, now).toMinutes());
                    }

                    else
                        newWaitingTime = Duration.ofMinutes((long) defaultWaitingTime * (positionInQueue + 1) -
                                Duration.between(acquisitionTime, now).toMinutes());

                    //
                    if (newWaitingTime.toMinutes() <= 0) {
                        newWaitingTime = additionalTime;
                    }

                } else
                    throw new NotInQueueException();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + ticket.getState());
        }
        return newWaitingTime;
    }

    /**
     * Retrieve Buildings that can be reached (travel time distance less than maxDistance minutes) from given Position.
     * For each building is provided the number of people already in queue for that building
     * @param position User position
     * @param meansOfTransport picked MeansOfTransport to travel to Building
     * @return list of reachable Building and for each of them the number of people already in queue
     */
    @Override
    public Map<Building,Integer> getAvailableBuildings(Position position, MeansOfTransport meansOfTransport) {

        //TODO: should return number of people before me in queu for each avilable building

        Map<Building,Integer> availableBuilding = new HashMap<>();

        for(Building building : dataAccess.retrieveBuildings()){
            Duration d = mapsAdapter.retrieveTravelTimeToBuilding(meansOfTransport,position,building.getAddress());

            if( d.toMinutes() < maxDistance.toMinutes()){
                List<LineUpDigitalTicket> tickets = dataAccess.retrieveTicketsInQueue(building.getBuildingID());
                availableBuilding.put(building,tickets.size());
            }

        }

        return availableBuilding;
    }

    @Override
    public List<Building> retrieveBuilding(int activityId) {
        List<Building> buildingOfActivity = new ArrayList<>();
        for (Building b : dataAccess.retrieveBuildings()){
            if (b.getActivity().getId() == activityId)
                buildingOfActivity.add(b);
        }
        return buildingOfActivity;

    }

    public QueueManagerInterface getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    public TimeSlotManagerInterface getTimeSlotManager() {
        return timeSlotManager;
    }

    public void setTimeSlotManager(TimeSlotManager timeSlotManager) {
        this.timeSlotManager = timeSlotManager;
    }

    public TicketValidationInt getTicketManager() {
        return ticketManager;
    }

    public void setTicketManager(TicketValidationInt ticketManager) {
        this.ticketManager = ticketManager;
    }

    public BuildingDataAccessInterface getDataAccess() {
        return dataAccess;
    }

    public void setDataAccess(BuildingDataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    //methods used by ticket manager

    @Override
    public boolean checkBuildingNotFull (int buildingID) {
        return dataAccess.retrieveBuildingState(buildingID);
    }

    @Override
    public void reduceCapacity(int buildingID) {
        Building building = dataAccess.retrieveBuilding(buildingID);
        int actualCapacity = building.getActualCapacity();
        building.setActualCapacity(actualCapacity - 1);
    }

    private void validateNext(int buildingId){
        LineUpDigitalTicket nextTicket = queueManager.getNext(buildingId);

        if(nextTicket != null) {
            ticketManager.validateTicket(nextTicket.getTicketID());
            queueManager.removeFromQueue(nextTicket.getTicketID());
        }
        else {
            Building building = dataAccess.retrieveBuilding(buildingId);
            building.increaseActualCapacity();
        }
    }

}
