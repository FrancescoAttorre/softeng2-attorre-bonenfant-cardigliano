package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.List;

@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@MappedSuperclass
@Entity
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    public abstract List<DigitalTicket> getDigitalTickets();
    public abstract void setDigitalTickets(List<DigitalTicket> digitalTickets);
}
