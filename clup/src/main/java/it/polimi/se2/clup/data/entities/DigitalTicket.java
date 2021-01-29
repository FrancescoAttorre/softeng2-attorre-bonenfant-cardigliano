package it.polimi.se2.clup.data.entities;

import javax.persistence.*;

@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Entity
@NamedQueries({
        @NamedQuery(name = "DigitalTicket.retrieveTicketById", query = "SELECT dt FROM DigitalTicket dt WHERE dt.ticketID = :ticketID "),
})
public class DigitalTicket {

    @Id
    @Column(unique = true, nullable = false)
    private int ticketID;

    @Enumerated(EnumType.STRING)
    private TicketState state;

    @ManyToOne(fetch = FetchType.EAGER)
    private Building building;

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
}
