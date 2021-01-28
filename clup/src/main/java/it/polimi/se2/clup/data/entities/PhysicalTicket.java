package it.polimi.se2.clup.data.entities;

import javax.persistence.*;

@Entity
public class PhysicalTicket {

    @Id
    @Column(unique = true, nullable = false)
    private int number;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ASSOCIATEDDIGITALTICKET_TICKETID", nullable = false)
    private LineUpDigitalTicket associatedDigitalTicket;

    @ManyToOne(fetch = FetchType.EAGER)
    private StoreManager storeManager;

    public LineUpDigitalTicket getAssociatedDigitalTicket() {
        return associatedDigitalTicket;
    }
    public void setAssociatedDigitalTicket(LineUpDigitalTicket associatedDigitalTicket) {
        this.associatedDigitalTicket = associatedDigitalTicket;
    }

    public StoreManager getStoreManager() { return storeManager;}
    public void setStoreManager(StoreManager storeManager) { this.storeManager = storeManager;}

}
