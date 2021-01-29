package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "RegisteredAppCustomer.checkCredentials", query = "SELECT u FROM RegisteredAppCustomer u WHERE u.username = :username AND u.password = :password"),
        @NamedQuery(name = "RegisteredAppCustomer.findUserByUsername", query = "SELECT u FROM RegisteredAppCustomer u WHERE u.username = :username"),
})
public class RegisteredAppCustomer extends User {

    @Column(unique = true, nullable = false)
    private String username;

    @Column
    private String password;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "registeredOwner")
    private List<LineUpDigitalTicket> lineUpDigitalTickets;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
    private List<BookingDigitalTicket> bookingDigitalTickets;

    @Column
    private String position;

    @Column
    private boolean GPSPreference;

    @Column
    private String meansOfTransport;


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

    public RegisteredAppCustomer() {
        super();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<LineUpDigitalTicket> getLineUpDigitalTickets() {
        return lineUpDigitalTickets;
    }

    public void setLineUpDigitalTickets(List<LineUpDigitalTicket> digitalTickets) {
        this.lineUpDigitalTickets = digitalTickets;
    }

    public List<BookingDigitalTicket> getBookingDigitalTickets() {
        return bookingDigitalTickets;
    }

    public void setBookingDigitalTickets(List<BookingDigitalTicket> bookingDigitalTickets) {
        this.bookingDigitalTickets = bookingDigitalTickets;
    }
}
