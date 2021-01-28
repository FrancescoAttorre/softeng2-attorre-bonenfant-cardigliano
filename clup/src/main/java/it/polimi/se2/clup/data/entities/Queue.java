package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "Queue.selectQueueWithBuildingId", query = "SELECT q FROM Queue q WHERE q.building = :buildingId"),
})
public class Queue {
    @Id
    @OneToOne
    @JoinColumn(unique = true, nullable = false, name="BUILDING_ID")
    private Building building;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "queue")
    private List<LineUpDigitalTicket> queueTickets;

    public Building getBuilding() {
        return building;
    }
    public void setBuilding(Building building) {
        this.building = building;
    }

    public List<LineUpDigitalTicket> getQueueTickets() {
        return queueTickets;
    }
    public void setQueueTickets(List<LineUpDigitalTicket> queueTickets) {
        this.queueTickets = queueTickets;
    }

}