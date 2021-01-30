package it.polimi.se2.clup.auth.exceptions;

public class AuthorizationException extends TokenException{
    public AuthorizationException() {
        super("User is not authorized");
    }
}
