package it.polimi.se2.clup.web.api;

import it.polimi.se2.clup.auth.AuthFlag;
import it.polimi.se2.clup.auth.AuthManagerInt;
import it.polimi.se2.clup.auth.exceptions.TokenException;
import it.polimi.se2.clup.building.BuildingManagerInterface;
import it.polimi.se2.clup.data.entities.Building;
import it.polimi.se2.clup.data.entities.Department;
import it.polimi.se2.clup.data.entities.MeansOfTransport;
import it.polimi.se2.clup.externalServices.Position;
import it.polimi.se2.clup.web.api.serial.BuildingJSON;
import it.polimi.se2.clup.web.api.serial.Coordinates;
import it.polimi.se2.clup.web.api.serial.DepartmentJSON;
import it.polimi.se2.clup.web.api.serial.Message;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/buildings")
public class BuildingResource {

    @EJB
    AuthManagerInt am;

    @EJB
    BuildingManagerInterface bm;

    @Path("/address/{meansOfTransport}/{address}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBuildingsByAddress(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                                      @PathParam("meansOfTransport") String meansOfTransport,
                                      @PathParam("address") String address) {
        int id;
        Response response;

        if (token != null && meansOfTransport != null && address != null) {
            try {
                id = am.verifyToken(token, List.of(AuthFlag.REGISTERED, AuthFlag.UNREGISTERED));
            } catch (TokenException e) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
            }



            response = Response.status(Response.Status.OK).build();

        } else
            response = Response.status(Response.Status.BAD_REQUEST).build();


        return response;
    }

    @Path("/coordinates/{meansOfTransport}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBuildingsByCoordinates(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                                          @PathParam("meansOfTransport") MeansOfTransport meansOfTransport,
                                          Coordinates coordinates) {
        int id;
        Response response;

        if(token != null && meansOfTransport != null && coordinates != null) {
            try {
                id = am.verifyToken(token, List.of(AuthFlag.UNREGISTERED, AuthFlag.REGISTERED));
            } catch (TokenException e) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(new Message(e.getMessage())).build();
            }

            BigDecimal latitude = new BigDecimal(coordinates.latitude);
            BigDecimal longitude = new BigDecimal(coordinates.longitude);

            Map<Building, Integer> availableBuildings = bm.getAvailableBuildings(new Position(latitude, longitude), meansOfTransport);

            List<BuildingJSON> serialList = new ArrayList<>();

            System.out.println("Found " + availableBuildings.size() + " buildings");

            for(Building b: availableBuildings.keySet()) {
                BuildingJSON serialB = new BuildingJSON();
                serialB.name = b.getName();
                serialB.id = b.getBuildingID();

                for(Department dep : b.getDepartments()) {
                    DepartmentJSON depJSON = new DepartmentJSON();
                    depJSON.departmentID = dep.getDepartmentID();
                    depJSON.name = dep.getName();
                    serialB.departments = new ArrayList<>();
                    serialB.departments.add(depJSON);
                }

                //serialB.departments = b.getDepartments();

                serialB.waitingTime = availableBuildings.get(b);
                serialList.add(serialB);
            }
            response = Response.status(Response.Status.OK).entity(serialList).build();
        } else
            response = Response.status(Response.Status.BAD_REQUEST).build();

        return response;
    }

    @Path("{id}/{date}/{permanenceTime}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response requestTimeSlots(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                                     @PathParam("id") int buildingID, @PathParam("permanenceTime") int permanenceTimeInMinutes) {
        return Response.status(Response.Status.OK).build();
    }


}
