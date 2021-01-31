package it.polimi.se2.clup.data.entities;

import javax.persistence.*;
import java.util.List;


public class AppCustomer extends User{

    @Column
    private String position;

    @Column
    private boolean GPSPreference;

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

    public boolean isGPSPreference() {
        return GPSPreference;
    }

    public void setGPSPreference(boolean GPSpreference) {
        this.GPSPreference = GPSpreference;
    }

    public String getMeansOfTransport() {
        return meansOfTransport;
    }

    public void setMeansOfTransport(String meansOfTransport) {
        this.meansOfTransport = meansOfTransport;
    }


}
