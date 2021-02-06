package it.polimi.se2.clup.externalServices;

import it.polimi.se2.clup.data.entities.MeansOfTransport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class MapsServiceServerTest {

    @Test
    public void keepsCorrectlyCoordinates() {
        MapsServiceServer mapsService = new MapsServiceServer();

        Position position = mapsService.getLocation("via Palmanova, 49 Milano 20132");

        Assertions.assertNotNull(position);
        Assertions.assertNotNull(position.getEastLongitude());
        Assertions.assertNotNull(position.getNorthLatitude());
    }

    @Test
    public void returnsCorrectlyTravelTime() {

        MapsServiceServer mapsService = new MapsServiceServer();
        Duration travelTime;

        Position customerPosition = mapsService.getLocation("via Palmanova, 49 Milano 20132");
        Position buildingPosition = mapsService.getLocation("Via Palmanova, 69, 20132 Milano");

        Assertions.assertNotNull(buildingPosition);
        Assertions.assertNotNull(buildingPosition.getEastLongitude());
        Assertions.assertNotNull(buildingPosition.getNorthLatitude());

        travelTime = mapsService.retrieveTravelTimeToBuilding(MeansOfTransport.BIKE, customerPosition, buildingPosition);
        if (travelTime != null)
            System.out.println("Travel time computed in minutes: " + travelTime.toMinutes());
    }
}
