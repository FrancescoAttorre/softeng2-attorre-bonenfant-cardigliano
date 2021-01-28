package it.polimi.se2.clup.data.entities;

import javax.persistence.*;

@Entity
public class PhysicalTicket {

    @Id
    @Column(unique = true, nullable = false)
    private String number;

    @OneToOne(fetch = FetchType.EAGER)
    private DigitalTicket associatedDigitalTicket;

    @ManyToOne(fetch = FetchType.EAGER)
    private StoreManager storeManager;

    public DigitalTicket getAssociatedDigitalTicket() {
        return associatedDigitalTicket;
    }
    public void setAssociatedDigitalTicket(DigitalTicket associatedDigitalTicket) {
        this.associatedDigitalTicket = associatedDigitalTicket;
    }

    public StoreManager getStoreManager() { return storeManager;}
    public void setStoreManager(StoreManager storeManager) { this.storeManager = storeManager;}

}
