package it.polimi.se2.clup.data.entities;


import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "UnregisteredAppCustomer.selectAll", query = "SELECT u FROM UnregisteredAppCustomer u "),
})
public class UnregisteredAppCustomer extends User{

    @Temporal(TemporalType.DATE)
    private Date date;

    @Column
    private String position;

    @Column
    private boolean GPSPreference;

    @Column
    private String meansOfTransport;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "unregisteredOwner")
    private List<LineUpDigitalTicket> lineUpDigitalTickets;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isGPSPreference() {
        return GPSPreference;
    }

    public void setGPSPreference(boolean GPSPreference) {
        this.GPSPreference = GPSPreference;
    }

    public String getMeansOfTransport() {
        return meansOfTransport;
    }

    public void setMeansOfTransport(String meansOfTransport) {
        this.meansOfTransport = meansOfTransport;
    }

    public List<LineUpDigitalTicket> getLineUpDigitalTickets() {
        return lineUpDigitalTickets;
    }

    public void setLineUpDigitalTickets(List<LineUpDigitalTicket> digitalTickets) {
        this.lineUpDigitalTickets = digitalTickets;
    }
}
