package it.polimi.se2.clup.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Department {
    @Id
    @GeneratedValue
    private int departmentID;

    @Column
    private int surplusCapacity;

    @Column
    private String name;
    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    public void setSurplusCapacity(int surplusCapacity) {
        this.surplusCapacity = surplusCapacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSurplusCapacity() { return surplusCapacity; }


}
