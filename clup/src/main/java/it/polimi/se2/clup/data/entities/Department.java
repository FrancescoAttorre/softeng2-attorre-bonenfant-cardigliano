package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Objects;

@XmlRootElement
@Entity
public class Department {
    @XmlElement
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int departmentID;

    @Column
    private int surplusCapacity;

    @XmlElement
    @Column
    private String name;

    @ManyToOne
    private Building building;

    @ManyToMany(mappedBy = "departments")
    private List<BookingDigitalTicket> tickets;

    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    public void setSurplusCapacity(int surplusCapacity) {
        this.surplusCapacity = surplusCapacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSurplusCapacity() { return surplusCapacity; }

    public List<BookingDigitalTicket> getTickets() {
        return tickets;
    }

    public void setTickets(List<BookingDigitalTicket> tickets) {
        this.tickets = tickets;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public void addTicket(BookingDigitalTicket ticket){
        this.tickets.add(ticket);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final Department other = (Department) obj;
        if (!this.name.equals(other.name)) {
            return false;
        }

        if(this.building == null || other.building == null)
            return false;

        return this.building.getBuildingID() == other.building.getBuildingID();
    }
}
