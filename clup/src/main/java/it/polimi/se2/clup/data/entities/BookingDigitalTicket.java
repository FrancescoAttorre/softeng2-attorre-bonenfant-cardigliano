package it.polimi.se2.clup.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.sql.Time;

@Entity
public class BookingDigitalTicket extends DigitalTicket{

    public BookingDigitalTicket() {
        super();
    }

    @Column
    private Time arrivalTime;

    @Column
    private Time departureTime;

    @Column
    private Time permanenceTime;

    @ManyToOne(fetch = FetchType.EAGER)
    private RegisteredAppCustomer ownerCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    private TimeSlot timeSlot;

    public Time getArrivalTime() { return arrivalTime;}
    public void setArrivalTime (Time arrivalTime) { this.arrivalTime = arrivalTime;}

    public Time getDepartureTime() { return departureTime;}
    public void setDepartureTime (Time departureTime) { this.departureTime = departureTime;}

    public Time getPermanenceTime() { return permanenceTime;}
    public void setPermanenceTime (Time permanenceTime) { this.permanenceTime = permanenceTime;}

    public TimeSlot getTimeSlot() { return timeSlot;}
    public void setTimeSlot (TimeSlot timeSlot) { this.timeSlot = timeSlot;}


    //???
    public RegisteredAppCustomer getOwnerCustomer() { return ownerCustomer;}
    public void setOwnerCustomer (RegisteredAppCustomer ownerCustomer) { this.ownerCustomer = ownerCustomer;}

}
