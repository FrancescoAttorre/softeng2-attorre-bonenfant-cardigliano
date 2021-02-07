package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "Queue.selectTicketInQueueByBuildingId", query = "SELECT t FROM Queue q JOIN q.queueTickets t WHERE q.building.id = :buildingId ORDER BY t.acquisitionTime"),
})
/**
 * Queue related to a particular building, with a list of digital tickets of customers in queue for it
 */
public class Queue {
    @Id
    @OneToOne
    private Building building;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "queue", orphanRemoval = true)
    private List<LineUpDigitalTicket> queueTickets = new ArrayList<>();

    public Building getBuilding() {
        return building;
    }
    public void setBuilding(Building building) {
        this.building = building;
    }

    public List<LineUpDigitalTicket> getQueueTickets() {
        return queueTickets;
    }
    public void setQueueTickets(List<LineUpDigitalTicket> queueTickets) {this.queueTickets = queueTickets;}

    public void addQueueTickets(LineUpDigitalTicket queueTicket){
        this.queueTickets.add(queueTicket);
    }
}
