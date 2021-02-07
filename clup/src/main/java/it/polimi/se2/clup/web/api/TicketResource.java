package it.polimi.se2.clup.web.api;

import it.polimi.se2.clup.auth.AuthFlag;
import it.polimi.se2.clup.auth.AuthManagerInt;
import it.polimi.se2.clup.auth.exceptions.TokenException;
import it.polimi.se2.clup.building.BuildingManagerInterface;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;
import it.polimi.se2.clup.ticket.InvalidTicketInsertionException;
import it.polimi.se2.clup.ticket.NotInQueueException;
import it.polimi.se2.clup.ticket.TicketManagerInterface;
import it.polimi.se2.clup.web.api.serial.BookingRequest;
import it.polimi.se2.clup.web.api.serial.Message;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/ticket")
public class TicketResource {

    @EJB
    AuthManagerInt am;

    @EJB
    TicketManagerInterface tm;

    @EJB
    BuildingManagerInterface bm;

    @Path("/{buildingID}/")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLineUp(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                              @PathParam("buildingID") int buildingID) {

        int id;
        Response response;

        if(token != null) {
            try {
                id = am.verifyToken(token, List.of(AuthFlag.UNREGISTERED, AuthFlag.REGISTERED, AuthFlag.MANAGER));
            } catch(TokenException e) {
                response = Response.status(Response.Status.UNAUTHORIZED).build();
                return response;
            }

            boolean notFull = bm.checkBuildingNotFull(buildingID);
            LineUpDigitalTicket ticketToAddInQueue = null;

            try {
                switch (am.getAuthFlag(token)) {
                    case REGISTERED:
                        ticketToAddInQueue = tm.acquireRegCustomerLineUpTicket(id, buildingID, notFull);
                        break;
                    case UNREGISTERED:
                        ticketToAddInQueue = tm.acquireUnregCustomerLineUpTicket(id, buildingID, notFull);
                        break;
                    case MANAGER:
                        ticketToAddInQueue = tm.acquireStoreManagerTicket(id, buildingID, notFull);
                        break;
                    default:
                }
            }
            catch (InvalidTicketInsertionException e) {
                return Response.status(Response.Status.CONFLICT).entity(new Message (e.getMessage())).build();
            }

            if(ticketToAddInQueue != null) {
                bm.insertInQueue(ticketToAddInQueue);
            }

            response = Response.status(Response.Status.OK).build();

        } else
            response = Response.status(Response.Status.BAD_REQUEST).build();
        return response;
    }


    @Path("/booking/")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getBookingTicket(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                                     BookingRequest request) throws Exception {

        int id;

        Response response;

        if(token != null) {

            try {
                id = am.verifyToken(token, List.of(AuthFlag.REGISTERED));
            } catch(TokenException e) {
                response = Response.status(Response.Status.UNAUTHORIZED).build();
                return response;
            }

            //if(tm.acquireBookingTicket(id, request.builingID, request.date, request.timeSlotID, request.timeSlotLength, request.departments))
            //    response = Response.status(Response.Status.OK).build();
            //else
                response = Response.status(Response.Status.CONFLICT).build();

        } else
            response = Response.status(Response.Status.BAD_REQUEST).build();
        return response;
    }

    @Path("/discover/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discover(@HeaderParam(HttpHeaders.AUTHORIZATION) String token) {
        int id;
        boolean result;
        Response response;
        Map<LineUpDigitalTicket, Duration> map;

        if(token != null) {

            try {
                id = am.verifyToken(token, List.of(AuthFlag.REGISTERED, AuthFlag.UNREGISTERED));
            } catch(TokenException e) {
                response = Response.status(Response.Status.UNAUTHORIZED).build();
                return response;
            }
            try {

                switch (am.getAuthFlag(token)) {
                    case REGISTERED:
                        map = tm.getWaitingUpdateRegCustomer(id);
                        break;
                    case UNREGISTERED:
                        map = tm.getWaitingUpdateUnregCustomer(id);
                        break;
                    case MANAGER:
                        map = tm.getWaitingUpdateSM(id);
                        break;
                    default:
                        map = new HashMap<>();

                }
            } catch (NotInQueueException e) {
                response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Message(e.getMessage())).build();
            }
            response = Response.status(Response.Status.OK).build();

        } else
            response = Response.status(Response.Status.BAD_REQUEST).build();
        return response;
    }

}
