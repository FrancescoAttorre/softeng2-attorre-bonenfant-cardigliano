package it.polimi.se2.clup.externalServices;

import java.math.BigDecimal;

/**
 * Position class composed by two coordinates : eastLongitude and northLatitude
 */
public class Position {

    private final BigDecimal eastLongitude;
    private final BigDecimal northLatitude;

    public Position (BigDecimal northLatitude, BigDecimal eastLongitude) {
        this.eastLongitude = northLatitude;
        this.northLatitude = eastLongitude;
    }

    public BigDecimal getNorthLatitude() {
        return northLatitude;
    }

    public BigDecimal getEastLongitude() {
        return eastLongitude;
    }
}
