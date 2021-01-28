package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.Building;
import it.polimi.se2.clup.data.entities.BuildingState;

import java.util.List;

public interface BuildingDataAccessInterface {
    List<Building> retrieveBuildings();
    BuildingState retrieveBuildingState(int id);

    
}
