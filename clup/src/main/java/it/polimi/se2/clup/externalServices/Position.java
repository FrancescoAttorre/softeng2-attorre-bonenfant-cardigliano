package it.polimi.se2.clup.externalServices;

import java.math.BigDecimal;

public class Position {

    private final BigDecimal eastLongitude;
    private final BigDecimal northLatitude;

    public Position (BigDecimal coordinateNorth, BigDecimal coordinateWest) {
        this.eastLongitude = coordinateNorth;
        this.northLatitude = coordinateWest;
    }

    public BigDecimal getNorthLatitude() {
        return northLatitude;
    }

    public BigDecimal getEastLongitude() {
        return eastLongitude;
    }
}
