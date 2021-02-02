package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedQueries({
        @NamedQuery(name = "LineUpDigitalTicket.selectWithID",
                query = "select t from LineUpDigitalTicket t where t.ticketID = :ID"),
        @NamedQuery(name = "LineUpDigitalTicket.selectWithUnregID",
                query = "select t from LineUpDigitalTicket t where t.unregisteredOwner.id = :unregID"),
        @NamedQuery(name = "LineUpDigitalTicket.selectWithRegID",
                query = "select t from LineUpDigitalTicket t where t.registeredOwner.id = :regID"),
        @NamedQuery(name = "LineUpDigitalTicket.selectWithSMID",
                query = "select t from LineUpDigitalTicket t where t.storeManagerOwner.id = :SMID"),
})

@Entity
public class LineUpDigitalTicket extends DigitalTicket {

    public LineUpDigitalTicket() {
        super();
    }

    @Column
    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime acquisitionTime;

    @OneToOne(mappedBy = "associatedDigitalTicket")
    private PhysicalTicket associatedPhysicalTicket;

    @ManyToOne(fetch = FetchType.LAZY)
    private Queue queue;

    @ManyToOne
    private UnregisteredAppCustomer unregisteredOwner;

    @ManyToOne
    private RegisteredAppCustomer registeredOwner;

    @ManyToOne
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
