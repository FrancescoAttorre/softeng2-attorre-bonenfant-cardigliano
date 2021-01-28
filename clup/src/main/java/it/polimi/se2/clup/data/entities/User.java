package it.polimi.se2.clup.data.entities;

import javax.persistence.*;

@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@MappedSuperclass
public class User {
    //abstract ?

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
}
