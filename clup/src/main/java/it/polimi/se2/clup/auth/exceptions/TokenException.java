package it.polimi.se2.clup.auth.exceptions;

public class TokenException extends Exception{
    public TokenException() {
        super("Invalid Token");
    }

    public TokenException(String message) {
        super(message);
    }
}
