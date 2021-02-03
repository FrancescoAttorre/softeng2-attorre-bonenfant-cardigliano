package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.List;

@Inheritance(strategy=InheritanceType.JOINED)
@Entity
@NamedQueries({
        @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    //@OneToMany(fetch = FetchType.LAZY)
    //private List<DigitalTicket> digitalTickets;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
