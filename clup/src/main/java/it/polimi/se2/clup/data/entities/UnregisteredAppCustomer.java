package it.polimi.se2.clup.data.entities;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "UnregisteredAppCustomer.findAll", query = "SELECT u FROM UnregisteredAppCustomer u "),
})

/**
 * Class of unregistered customers, that queue for a building without registering to the app, subclass of user
 */
public class UnregisteredAppCustomer extends User {

    private LocalDateTime date;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "unregisteredOwner",orphanRemoval = true)
    private List<LineUpDigitalTicket> lineUpDigitalTickets = new ArrayList<>();

    public void addLineUpTicket(LineUpDigitalTicket lineUpTicket){
        this.lineUpDigitalTickets.add(lineUpTicket);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<LineUpDigitalTicket> getLineUpDigitalTickets() {
        return lineUpDigitalTickets;
    }

    public void setLineUpDigitalTickets(List<LineUpDigitalTicket> digitalTickets) {
        this.lineUpDigitalTickets = digitalTickets;
    }
}
