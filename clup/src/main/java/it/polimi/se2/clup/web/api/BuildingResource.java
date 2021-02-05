package it.polimi.se2.clup.web.api;

import it.polimi.se2.clup.auth.AuthFlag;
import it.polimi.se2.clup.auth.AuthManagerInt;
import it.polimi.se2.clup.auth.exceptions.TokenException;
import it.polimi.se2.clup.building.BuildingManagerInterface;
import it.polimi.se2.clup.web.api.serial.Coordinates;
import it.polimi.se2.clup.web.api.serial.Message;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ResourceBundle;

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
                id = am.verifyToken(token, AuthFlag.UNREGISTERED);
            } catch (TokenException e) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
            }

            //retrieve building list

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
                                          @PathParam("meansOfTransport") String meansOfTransport,
                                          Coordinates coordinates) {
        int id;
        Response response;

        if(token != null && meansOfTransport != null && coordinates != null) {
            try {
                id = am.verifyToken(token, AuthFlag.UNREGISTERED);
            } catch (TokenException e) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(new Message(e.getMessage())).build();
            }

            response = Response.status(Response.Status.OK).build();
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
