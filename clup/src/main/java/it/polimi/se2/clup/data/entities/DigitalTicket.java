package it.polimi.se2.clup.data.entities;

import javax.persistence.*;

@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Entity
public class DigitalTicket {

    @Id
    @Column(unique = true, nullable = false)
    private int ticketID;

    @Enumerated(EnumType.STRING)
    private TicketState state;

    @ManyToOne(fetch = FetchType.EAGER)
    private AppCustomer ownerCustomer;

    @ManyToOne(fetch = FetchType.EAGER)
    private Building building;

    public AppCustomer getOwnerCustomer() { return ownerCustomer;}
    public void setOwnerCustomer (AppCustomer ownerCustomer) { this.ownerCustomer = ownerCustomer;}

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
