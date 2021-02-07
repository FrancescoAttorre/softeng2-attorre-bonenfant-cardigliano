package it.polimi.se2.clup.web.api;

import it.polimi.se2.clup.auth.AuthFlag;
import it.polimi.se2.clup.auth.AuthManagerInt;
import it.polimi.se2.clup.auth.exceptions.TokenException;
import it.polimi.se2.clup.building.BuildingManagerInterface;
import it.polimi.se2.clup.data.entities.BookingDigitalTicket;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;
import it.polimi.se2.clup.ticket.InvalidTicketInsertionException;
import it.polimi.se2.clup.ticket.NotInQueueException;
import it.polimi.se2.clup.ticket.TicketManagerInterface;
import it.polimi.se2.clup.web.api.serial.BookingRequest;
import it.polimi.se2.clup.web.api.serial.Message;
import it.polimi.se2.clup.web.api.serial.TicketJSON;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.ArrayList;
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

            boolean isFull = !bm.checkBuildingNotFull(buildingID);
            LineUpDigitalTicket ticketToAddInQueue = null;

            try {
                switch (am.getAuthFlag(token)) {
                    case REGISTERED:
                        ticketToAddInQueue = tm.acquireRegCustomerLineUpTicket(id, buildingID, isFull);
                        break;
                    case UNREGISTERED:
                        ticketToAddInQueue = tm.acquireUnregCustomerLineUpTicket(id, buildingID, isFull);
                        break;
                    case MANAGER:
                        ticketToAddInQueue = tm.acquireStoreManagerTicket(id, buildingID, isFull);
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

        if(token != null && request != null) {

            try {
                id = am.verifyToken(token, List.of(AuthFlag.REGISTERED));
            } catch(TokenException e) {
                response = Response.status(Response.Status.UNAUTHORIZED).build();
                return response;
            }



            if(tm.acquireBookingTicket(id, request.buildingID, request.date, request.timeSlotID, request.timeSlotLength, request.departments))
                response = Response.status(Response.Status.OK).build();
            else
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
                id = am.verifyToken(token, List.of(AuthFlag.REGISTERED, AuthFlag.UNREGISTERED, AuthFlag.MANAGER));
            } catch(TokenException e) {
                response = Response.status(Response.Status.UNAUTHORIZED).entity(new Message(e.getMessage())).build();
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
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Message(e.getMessage())).build();
            }

            List<TicketJSON> listSerial = new ArrayList<>();
            for(LineUpDigitalTicket t: map.keySet()) {
                TicketJSON ticketJSON = new TicketJSON();
                ticketJSON.id = t.getTicketID();
                ticketJSON.buildingID = t.getBuilding().getBuildingID();
                ticketJSON.userID = id;
                ticketJSON.waitingTime = (int) map.get(t).toMinutes();

                listSerial.add(ticketJSON);
            }

            response = Response.status(Response.Status.OK).entity(listSerial).build();

        } else
            response = Response.status(Response.Status.BAD_REQUEST).build();
        return response;
    }

    @Path("check/{userID}/{bookingID}{ticketID}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkTicket(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("userID") int userID,
                                   @PathParam("bookingID") int bookingID,
                                   @PathParam("ticketID") int ticketID) {
        Response response;
        boolean result;

        if(token != null) {
            try {
                am.verifyToken(token, List.of(AuthFlag.MANAGER));
            } catch (TokenException e) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(new Message(e.getMessage())).build();
            }

            List<BookingDigitalTicket> bookingTickets = tm.getBookingTicketsRegCustomer(userID);
            result = bm.customerEntry(ticketID, bookingID, userID, bookingTickets);

            if(result)
                response = Response.status(Response.Status.OK).build();
            else
                response = Response.status(Response.Status.CONFLICT).build();
        } else
            response = Response.status(Response.Status.BAD_REQUEST).build();

        return response;
    }

    @Path("/exit/{buildingID}/{ticketID}")
    @POST
    public Response customerExit(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                                 @PathParam("buildingID") int buildingID, @PathParam("ticketID") int ticketID) {

        Response response;

        if(token != null) {
            try {
                am.verifyToken(token, List.of(AuthFlag.MANAGER));
            } catch(TokenException e) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            bm.customerExit(buildingID, ticketID);
            response = Response.status(Response.Status.OK).build();
        } else {
            response = Response.status(Response.Status.BAD_REQUEST).build();
        }
        return response;
    }



}
