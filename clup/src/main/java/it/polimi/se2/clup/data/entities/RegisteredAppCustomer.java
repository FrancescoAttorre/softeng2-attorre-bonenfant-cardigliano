package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "RegisteredAppCustomer.findUserByUsername", query = "SELECT u FROM RegisteredAppCustomer u WHERE u.username = :username"),
        @NamedQuery(name = "RegisteredAppCustomer.findAll", query = "SELECT u FROM RegisteredAppCustomer u "),
})

/**
 * Subclass of user, customers registered in the app through username and password
 */
public class RegisteredAppCustomer extends User {

    @Column(unique = true, nullable = false)
    private String username;

    @Column
    private String password;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "registeredOwner",orphanRemoval = true)
    private List<LineUpDigitalTicket> lineUpDigitalTickets = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<BookingDigitalTicket> bookingDigitalTickets = new ArrayList<>();


    public void addLineUpTicket(LineUpDigitalTicket lineUpTicket){
        this.lineUpDigitalTickets.add(lineUpTicket);
    }

    public void addBookingTicket(BookingDigitalTicket bookingTicket){
        this.bookingDigitalTickets.add(bookingTicket);
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
