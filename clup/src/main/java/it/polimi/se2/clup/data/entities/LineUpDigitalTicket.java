package it.polimi.se2.clup.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class LineUpDigitalTicket extends DigitalTicket {

    //associated to unregistered customers + QUEUE

    @Column(unique = true, nullable = false)
    private String number;

    public LineUpDigitalTicket() {
        super();
    }


}
