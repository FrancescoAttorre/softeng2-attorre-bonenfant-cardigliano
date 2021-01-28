package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class LineUpDigitalTicket extends DigitalTicket {

    public LineUpDigitalTicket() {
        super();
    }

    @Column(unique = true, nullable = false)
    private int number;

    @ManyToOne(fetch = FetchType.LAZY)
    private Queue queue;

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public Queue getQueue() {
        return queue;
    }
    public void setQueue(Queue queue) {
        this.queue = queue;
    }

}
