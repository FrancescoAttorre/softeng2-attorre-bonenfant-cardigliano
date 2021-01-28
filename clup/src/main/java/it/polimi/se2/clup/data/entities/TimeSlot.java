package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.sql.Time;
import java.util.List;

@Entity
public class TimeSlot {
    @Id
    @Column(unique = true, nullable = false)
    private int slotID;

    @Column(nullable = false)
    private Time startingTime;

    @Column(nullable = false)
    private Time endingTime;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "timeSlot")
    private List<BookingDigitalTicket> bookingDigitalTickets;

    public int getSlotID() { return slotID;}
    public void setSlotID (int slotID) { this.slotID = slotID;}

    public Time getStartingTime() { return startingTime;}
    public void setStartingTime (Time startingTime) { this.startingTime = startingTime;}

    public Time getEndingTime() { return endingTime;}
    public void setEndingTime (Time endingTime) { this.endingTime = endingTime;}

    public void setBookingDigitalTickets(List<BookingDigitalTicket> bookingDigitalTickets) {
        this.bookingDigitalTickets = bookingDigitalTickets;
    }
    public List<BookingDigitalTicket> getBookingDigitalTickets () {
        return bookingDigitalTickets;
    }
}
