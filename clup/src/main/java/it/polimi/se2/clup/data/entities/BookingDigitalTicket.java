package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NamedQueries({
        @NamedQuery(name = "BookingDigitalTicket.selectByBuildingIdAndDate", query = "SELECT b FROM BookingDigitalTicket b WHERE b.date = :date AND b.building = :buildingId "),
})
public class BookingDigitalTicket extends DigitalTicket{

    public BookingDigitalTicket() {
        super();
    }

    @Column
    private LocalDate date;

    @Column
    private int timeSlotID;

    @Column
    private int timeSlotLength;

    @ManyToOne(fetch = FetchType.EAGER)
    private RegisteredAppCustomer owner;

    public int getTimeSlotID() {
        return timeSlotID;
    }

    public void setTimeSlotID(int timeSlotID) {
        this.timeSlotID = timeSlotID;
    }

    public int getTimeSlotLength() {
        return timeSlotLength;
    }

    public void setTimeSlotLength(int timeSlotLength) {
        this.timeSlotLength = timeSlotLength;
    }

    public LocalDate getDate() { return date;}
    public void setDate (LocalDate date) { this.date = date;}

    public RegisteredAppCustomer getOwner() { return owner;}
    public void setOwner(RegisteredAppCustomer ownerCustomer) { this.owner = ownerCustomer;}
}
