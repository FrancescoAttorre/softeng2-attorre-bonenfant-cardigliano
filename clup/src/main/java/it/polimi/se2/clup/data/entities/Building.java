package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "Building.retrieveByAccessCode", query = "SELECT u FROM Building  u WHERE u.accessCode = :accessCode"),
        @NamedQuery(name = "Building.retrieveBuildingByName", query = "SELECT u FROM Building u WHERE u.id = :buildingName "),
        @NamedQuery(name = "Building.findAll", query = "SELECT b FROM Building b"),
})
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private int capacity;

    @OneToOne
    private Queue queue;

    @ManyToOne
    private Activity activity;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "building",cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Department> departments = new ArrayList<>();

    @Column(nullable = false)
    private LocalTime opening;

    @Column(nullable = false)
    private LocalTime closing;

    @Column(unique = true, nullable = false)
    private String accessCode;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="building")
    private List<StoreManager> managers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="building")
    private List<DigitalTicket> tickets;

    @Column
    private int actualCapacity;

    @Column
    private Duration deltaExitTime; // seconds between the last exit time of last exits

    @Column
    private int lastExitTime; // seconds between 00:00 and actual time

    public Duration getDeltaExitTime() {
        return deltaExitTime;
    }

    public void setDeltaExitTime(Duration deltaExitTime) {
        this.deltaExitTime = deltaExitTime;
    }

    public int getLastExitTime() {
        return lastExitTime;
    }

    public void setLastExitTime(int lastExitTime) {
        this.lastExitTime = lastExitTime;
    }

    public int getActualCapacity() {
        return actualCapacity;
    }

    public void addDepartment(Department department) {
        this.departments.add(department);
    }

    public void increaseActualCapacity(){
        this.actualCapacity++;
    }

    public void setActualCapacity(int actualCapacity) {
        this.actualCapacity = actualCapacity;
    }

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

    public LocalTime getOpening() {
        return opening;
    }

    public void setOpening(LocalTime opening) {
        this.opening = opening;
    }

    public LocalTime getClosing() {
        return closing;
    }

    public void setClosing(LocalTime closing) {
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
        this.id = buildingID;
    }

    public int getBuildingID() {
        return id;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public List<StoreManager> getManagers() {
        return managers;
    }

    public void setManagers(List<StoreManager> managers) {
        this.managers = managers;
    }


    public List<DigitalTicket> getTickets() {
        return tickets;
    }

    public void setTickets(List<DigitalTicket> tickets) {
        this.tickets = tickets;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }
}
