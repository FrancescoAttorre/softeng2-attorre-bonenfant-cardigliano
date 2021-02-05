package it.polimi.se2.clup.externalServices;

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

public class MapsServiceServer implements MapsServiceServerAdapter {

    private static final String geocode = "https://api.openrouteservice.org/geocode/search?api_key=5b3ce3597851110001cf6248534e8b28978045ac91bf022ed129b404&text=Namibian%20Brewery";
    private static final String directions = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=5b3ce3597851110001cf6248534e8b28978045ac91bf022ed129b404&start=8.681495,49.41461&end=8.687872,49.420318" ;

    @Override
    public Duration retrieveBuildingDistance (Position position, String buildingAddress) {

        Client client = ClientBuilder.newClient();
        Response response = client.target(directions)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .get();

        System.out.println("status: " + response.getStatus());
        System.out.println("headers: " + response.getHeaders());
        System.out.println("body:" + response.readEntity(String.class));
        return null;
    }

    @Override
    public Position getLocation(String address) {

        Client client = ClientBuilder.newClient();
        Response response = client.target(geocode)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .get();

        System.out.println("status: " + response.getStatus());
        System.out.println("headers: " + response.getHeaders());

        JsonObject jsonObject = Json.createReader(new StringReader(response.readEntity(String.class))).readObject();

        JsonArray internalJson = (JsonArray) jsonObject.get("features");
        JsonObject internalJsonObj = (JsonObject) internalJson.get(1);
        JsonObject moreInternalJson = (JsonObject) internalJsonObj.get("geometry");
        List<JsonNumber> coordinates = (List<JsonNumber>) moreInternalJson.get("coordinates");

        return new Position(coordinates.get(0).bigDecimalValue(), coordinates.get(1).bigDecimalValue());

    }

}
