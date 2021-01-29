package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.Date;
import java.time.Duration;

@Entity
public class LineUpDigitalTicket extends DigitalTicket {

    public LineUpDigitalTicket() {
        super();
    }

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date acquisitionTime;

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

    @Column
    private Duration estimatedWaitingTime;

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

    public Date getAcquisitionTime() {
        return acquisitionTime;
    }

    public void setAcquisitionTime(Date acquisitionTime) {
        this.acquisitionTime = acquisitionTime;
    }

    public Duration getEstimatedWaitingTime() {
        return estimatedWaitingTime;
    }

    public void setEstimatedWaitingTime(Duration estimatedWaitingTime) {
        this.estimatedWaitingTime = estimatedWaitingTime;
    }
}
