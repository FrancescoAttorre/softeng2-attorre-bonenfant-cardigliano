package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
public class LineUpDigitalTicket extends DigitalTicket {

    public LineUpDigitalTicket() {
        super();
    }

    @Column(unique = true, nullable = false)
    private int number;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "associatedDigitalTicket")
    private PhysicalTicket associatedPhysicalTicket;

    @ManyToOne(fetch = FetchType.LAZY)
    private Queue queue;

    @ManyToOne(fetch = FetchType.EAGER)
    private User owner;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date acquisitionTime;

    public PhysicalTicket getAssociatedPhysicalTicket() { return associatedPhysicalTicket;}
    public void setAssociatedPhysicalTicket(PhysicalTicket associatedPhysicalTicket) { this.associatedPhysicalTicket = associatedPhysicalTicket;}

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public Queue getQueue() {
        return queue;
    }
    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public User getOwner() { return owner;}
    public void setOwner(User ownerCustomer) { this.owner = ownerCustomer;}
}
