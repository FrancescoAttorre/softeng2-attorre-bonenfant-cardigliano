package it.polimi.se2.clup.externalServices;

import java.time.Duration;

public interface MapsServiceServerAdapter {

    Duration retrieveBuildingDistance (Position position, String buildingAddress);
    Position getLocation(String address);

}
