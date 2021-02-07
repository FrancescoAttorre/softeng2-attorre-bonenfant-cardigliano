package it.polimi.se2.clup.data.entities;

import javax.persistence.*;

/**
 * PhysicalTicket with a specific number, owned by customers physically present on site and related to
 * a line up digital ticket
 */

@Entity
public class PhysicalTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
