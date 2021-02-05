package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.List;

@NamedQueries({
        @NamedQuery(name = "Activity.selectWithName",
                query = "select a from Activity a where a.name = :name"),
        @NamedQuery(name = "Activity.selectWithPIVA",
                query = "select a from Activity a where a.pIva = :pIva"),
        @NamedQuery(name = "Activity.selectAll",
                query = "select a from Activity a")

})

@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String pIva;

    private String password;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "activity")
    private List<Building> buildings;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setpIva(String pIva) {
        this.pIva = pIva;
    }

    public String getpIva() {
        return pIva;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
