package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.List;

@NamedQuery(name="StoreManager.getBuildingAccessCode",
            query="Select b.accessCode from Building b where b = :building")
@DiscriminatorValue("STORE_MANAGER")
@Entity
public class StoreManager extends User {

    @ManyToOne
    @JoinColumn(name="BUILDING_ID")
    private Building building;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "storeManager")
    private List<PhysicalTicket> physicalTickets;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "storeManagerOwner")
    private List<LineUpDigitalTicket> lineUpDigitalTickets;

    public void addLineUpTicket(LineUpDigitalTicket lineUpTicket){
        this.lineUpDigitalTickets.add(lineUpTicket);
    }

    public void addPhysicalTicket(PhysicalTicket physicalTicket){
        this.physicalTickets.add(physicalTicket);
    }

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

    public List<LineUpDigitalTicket> getLineUpDigitalTickets() {
        return lineUpDigitalTickets;
    }
    public void setLineUpDigitalTickets(List<LineUpDigitalTicket> digitalTickets) {
        this.lineUpDigitalTickets = digitalTickets;
    }
}