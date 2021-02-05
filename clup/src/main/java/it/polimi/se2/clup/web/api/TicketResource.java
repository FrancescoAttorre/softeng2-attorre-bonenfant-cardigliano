package it.polimi.se2.clup.web.api;

import it.polimi.se2.clup.web.api.serial.BookingRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ticket")
public class TicketResource {

    @Path("/lineup/{buildingID}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void getLineUp(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                              @PathParam("buildingID") int buildingID) {

    }

    @Path("/booking/")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getBookingTicket(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                                     BookingRequest request) {

    }

    @Path("/discover")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void discover(@HeaderParam(HttpHeaders.AUTHORIZATION) String token) {

    }

}
