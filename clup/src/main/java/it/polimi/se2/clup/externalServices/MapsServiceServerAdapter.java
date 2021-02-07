package it.polimi.se2.clup.externalServices;

import it.polimi.se2.clup.data.entities.MeansOfTransport;

import java.time.Duration;

/**
 * Interface implemented by mapsServiceServer, acting as an intermediary with the external service
 * in order to implement the methods by which it is used based on it
 */
public interface MapsServiceServerAdapter {

    Duration retrieveTravelTimeToBuilding(MeansOfTransport meansOfTransport, Position customerPosition, String buildingAddress);
    Position getLocation(String address);

}
