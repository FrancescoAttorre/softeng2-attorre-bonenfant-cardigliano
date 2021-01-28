package it.polimi.se2.clup.data.entities;

import javax.persistence.*;

@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Entity
public class AppCustomer extends User{

    @Column
    private String position;

    @Column
    private boolean GPSpreference;

    @Column
    private String meansOfTransport;

    public AppCustomer() {
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isGPSpreference() {
        return GPSpreference;
    }

    public void setGPSpreference(boolean GPSpreference) {
        this.GPSpreference = GPSpreference;
    }

    public String getMeansOfTransport() {
        return meansOfTransport;
    }

    public void setMeansOfTransport(String meansOfTransport) {
        this.meansOfTransport = meansOfTransport;
    }
}
