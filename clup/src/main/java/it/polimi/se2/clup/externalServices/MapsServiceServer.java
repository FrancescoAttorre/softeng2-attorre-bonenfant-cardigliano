package it.polimi.se2.clup.externalServices;

import it.polimi.se2.clup.data.entities.MeansOfTransport;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import java.io.StringReader;
import java.time.Duration;
import java.util.List;

@Stateless
public class MapsServiceServer implements MapsServiceServerAdapter {

    private static final String apiKey = "?api_key=5b3ce3597851110001cf6248534e8b28978045ac91bf022ed129b404";
    private static final String car = "driving-car";
    private static final String cycling = "cycling-regular";
    private static final String walking = "foot-walking";
    private static final String geocode = "https://api.openrouteservice.org/geocode/search" + apiKey + "&text=";
    private static final String directions = "https://api.openrouteservice.org/v2/directions/" ;
    private static final String startingCoordinates = "&start=";
    private static final String endingCoordinates = "&end=";
    private static final String spaceChar = "%20";

    @Override
    public Duration retrieveTravelTimeToBuilding (MeansOfTransport meansOfTransport, Position customerPosition, String buildingAddress) {

        Position buildingPosition = getLocation(buildingAddress);

        String target = directions;
        switch (meansOfTransport) {
            case CAR:
                target = target + car;
                break;
            case BIKE:
                target = target + cycling;
                break;
            case WALKING:
                target = target + walking;
                break;
        }

        target = target + apiKey;
        target = target + startingCoordinates + customerPosition.getEastLongitude() + "," + customerPosition.getNorthLatitude();
        target = target + endingCoordinates + buildingPosition.getEastLongitude() + "," + buildingPosition.getNorthLatitude();


        Client client = ClientBuilder.newClient();
        Response response = client.target(target)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .get();

        JsonObject jsonObject = Json.createReader(new StringReader(response.readEntity(String.class))).readObject();
        try  {
            JsonArray internalJson = (JsonArray) jsonObject.get("features");
            JsonObject internalJsonObj = (JsonObject) internalJson.get(0);
            JsonObject propertiesJson = (JsonObject) internalJsonObj.get("properties");
            JsonObject summaryJson = (JsonObject) propertiesJson.get("summary");
            double decimalDuration = Double.parseDouble(summaryJson.get("duration").toString());

            return Duration.ofSeconds((int) decimalDuration);
        }
        catch (NullPointerException e) {
            JsonObject error = (JsonObject) jsonObject.get("error");
            System.out.println(error.get("message").toString());
            return null;
        }
    }

    @Override
    public Position getLocation(String address) {

        address = address.replace(" ", spaceChar);

        String target = geocode + address;

        Client client = ClientBuilder.newClient();
        Response response = client.target(target)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .get();

        System.out.println("status: " + response.getStatus());
        System.out.println("headers: " + response.getHeaders());

        JsonObject jsonObject = Json.createReader(new StringReader(response.readEntity(String.class))).readObject();

        JsonArray internalJson = (JsonArray) jsonObject.get("features");
        JsonObject internalJsonObj = (JsonObject) internalJson.get(0);
        JsonObject moreInternalJson = (JsonObject) internalJsonObj.get("geometry");
        List<JsonNumber> coordinates = (List<JsonNumber>) moreInternalJson.get("coordinates");

        return new Position(coordinates.get(0).bigDecimalValue(), coordinates.get(1).bigDecimalValue());

    }

}
