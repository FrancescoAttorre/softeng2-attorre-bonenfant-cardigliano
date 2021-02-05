package it.polimi.se2.clup.externalServices;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MapsServiceServerTest {

    @Test
    public void keepsCorrectlyCoordinates() {
        MapsServiceServer mapsService = new MapsServiceServer();

        Assertions.assertNotNull(mapsService.getLocation("via Palmanova, 49 Milano 20132"));

    }
}
