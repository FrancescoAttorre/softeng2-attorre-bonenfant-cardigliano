package it.polimi.se2.clup.externalServices;

import it.polimi.se2.clup.data.entities.MeansOfTransport;

import java.time.Duration;

public interface MapsServiceServerAdapter {

    Duration retrieveTravelTimeToBuilding(MeansOfTransport meansOfTransport, Position customerPosition, Position buildingAddress);
    Position getLocation(String address);

}
