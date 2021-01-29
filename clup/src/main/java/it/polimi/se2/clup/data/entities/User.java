package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.List;

@Inheritance(strategy=InheritanceType.JOINED)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToMany(fetch = FetchType.LAZY)
    private List<DigitalTicket> digitalTickets;


    public List<DigitalTicket> getDigitalTickets() {
        return digitalTickets;
    }

    public void setDigitalTickets(List<DigitalTicket> digitalTickets) {
        this.digitalTickets = digitalTickets;
    }
}
