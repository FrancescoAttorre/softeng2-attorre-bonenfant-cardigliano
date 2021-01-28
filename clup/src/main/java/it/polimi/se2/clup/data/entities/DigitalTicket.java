package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.List;

@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Entity
public class DigitalTicket {

    @Id
    @Column(unique = true, nullable = false)
    private int ticketID;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "associatedDigitalTicket")
    private PhysicalTicket associatedPhysicalTicket;

    @ManyToOne(fetch = FetchType.EAGER)
    private AppCustomer ownerCustomer;

    public PhysicalTicket getAssociatedPhysicalTicket() { return associatedPhysicalTicket;}
    public void setAssociatedPhysicalTicket(PhysicalTicket associatedPhysicalTicket) { this.associatedPhysicalTicket = associatedPhysicalTicket;}

    public AppCustomer getOwnerCustomer() { return ownerCustomer;}
    public void setOwnerCustomer (AppCustomer ownerCustomer) { this.ownerCustomer = ownerCustomer;}
}
