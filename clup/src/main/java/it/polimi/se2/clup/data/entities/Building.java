package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.sql.Time;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "Building.retrieveBuildingById", query = "SELECT u FROM Building u WHERE u.id = :buildingId "),
        @NamedQuery(name = "Building.findAll", query = "SELECT b FROM Building b"),
})
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String name;

    @Column
    private String address;

    @Column
    private int capacity;

    @OneToOne
    private Queue queue;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy="building")
    private List<StoreManager> managers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="building")
    private List<DigitalTicket> tickets;

    @Column
    private int actualCapacity;

    public int getActualCapacity() {
        return actualCapacity;
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
