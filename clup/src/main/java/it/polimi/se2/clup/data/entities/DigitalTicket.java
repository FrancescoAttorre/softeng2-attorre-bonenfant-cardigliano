package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Entity
@NamedQueries({
        @NamedQuery(name = "DigitalTicket.retrieveTicketById", query = "SELECT dt FROM DigitalTicket dt WHERE dt.ticketID = :ticketID "),
})
public class DigitalTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ticketID;

    @Enumerated(EnumType.STRING)
    private TicketState state;

    @ManyToOne(fetch = FetchType.EAGER)
    private Building building;

    @Column
    private LocalDateTime validationTime;

    public LocalDateTime getValidationTime() {
        return validationTime;
    }

    public void setValidationTime(LocalDateTime validationTime) {
        this.validationTime = validationTime;
    }

    public TicketState getState() {
        return state;
    }
    public void setState(TicketState state) {
        this.state = state;
    }

    public Building getBuilding() {
        return building;
    }
    public void setBuilding(Building building) {
        this.building = building;
    }

    public int getTicketID() {
        return ticketID;
    }

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }
}
