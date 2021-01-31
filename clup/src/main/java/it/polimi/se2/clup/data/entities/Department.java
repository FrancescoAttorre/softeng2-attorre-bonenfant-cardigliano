package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int departmentID;

    @Column
    private int surplusCapacity;
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
}
