package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.sql.Time;
import java.util.List;

@Entity
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int buildingID;

    @Column
    private String name;

    @Column
    private String address;

    @Column
    private int capacity;

    @ManyToOne
    private Activity activity;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "building")
    private List<Department> departments;

    @Column
    private Time opening;

    @Column
    private Time closing;

    @Column
    private String accessCode;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Time getOpening() {
        return opening;
    }

    public void setOpening(Time opening) {
        this.opening = opening;
    }

    public Time getClosing() {
        return closing;
    }

    public void setClosing(Time closing) {
        this.closing = closing;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBuildingID(int buildingID) {
        this.buildingID = buildingID;
    }

    public int getBuildingID() {
        return buildingID;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
