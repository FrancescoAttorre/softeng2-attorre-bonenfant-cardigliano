package it.polimi.se2.clup.web.api;

import it.polimi.se2.clup.auth.AuthManagerInt;
import it.polimi.se2.clup.web.api.serial.ActivityCredentials;
import it.polimi.se2.clup.web.api.serial.Credentials;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/register")
public class RegisterResource {
    @EJB
    AuthManagerInt am;

    @Path("/user")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerCustomer(Credentials credentials) {

        if(credentials.username == null || credentials.password == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        if(am.registerUser(credentials.username, credentials.password))
            return Response.status(Response.Status.OK).build();

        return Response.status(Response.Status.CONFLICT).build();
    }

    @Path("/activity")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerActivity(ActivityCredentials credentials) {
        if(credentials.name == null || credentials.pIVA == null || credentials.password == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        if(am.registerActivity(credentials.name, credentials.pIVA, credentials.password))
            return Response.status(Response.Status.OK).build();

        return Response.status(Response.Status.CONFLICT).build();

    }
}
