package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.Building;
import it.polimi.se2.clup.data.entities.DigitalTicket;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;

import java.util.List;

public interface BuildingDataAccessInterface {
    List<Building> retrieveBuildings();
    boolean retrieveBuildingState(int id);
    void insertInQueue(LineUpDigitalTicket ticket);
}
