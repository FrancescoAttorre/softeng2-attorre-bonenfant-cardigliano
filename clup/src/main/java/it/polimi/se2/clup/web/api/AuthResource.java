package it.polimi.se2.clup.web.api;

import it.polimi.se2.clup.auth.AuthManagerInt;
import it.polimi.se2.clup.auth.exceptions.CredentialsException;
import it.polimi.se2.clup.web.api.serial.Credentials;
import it.polimi.se2.clup.web.api.serial.Token;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/auth/")
public class AuthResource {

    @EJB
    AuthManagerInt am;

    @Path("/registered")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(Credentials credentials) {
        if(credentials.username == null || credentials.password == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        String token;

        try {
            token = am.authenticate(credentials.username, credentials.password);
        } catch (CredentialsException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return Response.status(Response.Status.OK).entity(new Token(token)).build();

    }

    @Path("/unregistered")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDailyToken() {
        String token = am.createDailyToken();

        return Response.status(Response.Status.OK).entity(new Token(token)).build();

    }

    @Path("/storemanager/{buildingAccessCode}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticateStoreManager(@PathParam("buildingAccessCode") String accessCode) {
        String token;
        Response response;

        try {
            token = am.authenticate(accessCode);
            response = Response.status(Response.Status.OK).entity(new Token(token)).build();
        } catch (CredentialsException e) {
            response = Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return response;
    }
}
