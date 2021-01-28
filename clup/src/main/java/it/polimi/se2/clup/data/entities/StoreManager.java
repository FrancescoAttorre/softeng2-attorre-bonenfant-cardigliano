package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class StoreManager extends User {

    @ManyToOne
    @JoinColumn(name="BUILDING_ID")
    private Building building;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "storeManager")
    private List<PhysicalTicket> physicalTickets;

    public Building getBuilding() {
        return building;
    }
    public void setBuilding(Building building) {
        this.building = building;
    }

    public List<PhysicalTicket> getPhysicalTickets() {
        return physicalTickets;
    }
    public void setPhysicalTickets(List<PhysicalTicket> physicalTickets) {
        this.physicalTickets = physicalTickets;
    }
}