package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class LineUpDigitalTicket extends DigitalTicket {

    public LineUpDigitalTicket() {
        super();
    }

    @Column
    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime acquisitionTime;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "associatedDigitalTicket")
    private PhysicalTicket associatedPhysicalTicket;

    @ManyToOne(fetch = FetchType.LAZY)
    private Queue queue;

    @ManyToOne(fetch = FetchType.LAZY)
    private UnregisteredAppCustomer unregisteredOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    private RegisteredAppCustomer registeredOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    private StoreManager storeManagerOwner;

    public PhysicalTicket getAssociatedPhysicalTicket() { return associatedPhysicalTicket;}
    public void setAssociatedPhysicalTicket(PhysicalTicket associatedPhysicalTicket) { this.associatedPhysicalTicket = associatedPhysicalTicket;}

    public Queue getQueue() {
        return queue;
    }
    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public UnregisteredAppCustomer getUnregisteredOwner() {
        return unregisteredOwner;
    }

    public void setUnregisteredOwner(UnregisteredAppCustomer unregisteredOwner) {
        this.unregisteredOwner = unregisteredOwner;
    }

    public RegisteredAppCustomer getRegisteredOwner() {
        return registeredOwner;
    }

    public void setRegisteredOwner(RegisteredAppCustomer registeredOwner) {
        this.registeredOwner = registeredOwner;
    }

    public StoreManager getStoreManagerOwner() {
        return storeManagerOwner;
    }

    public void setStoreManagerOwner(StoreManager storeManagerOwner) {
        this.storeManagerOwner = storeManagerOwner;
    }

    public LocalDateTime getAcquisitionTime() {
        return acquisitionTime;
    }

    public void setAcquisitionTime(LocalDateTime acquisitionTime) {
        this.acquisitionTime = acquisitionTime;
    }
}
