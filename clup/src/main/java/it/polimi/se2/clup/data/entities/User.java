package it.polimi.se2.clup.data.entities;

import javax.persistence.*;

@Inheritance(strategy=InheritanceType.JOINED)
@Entity
@NamedQueries({
        @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
