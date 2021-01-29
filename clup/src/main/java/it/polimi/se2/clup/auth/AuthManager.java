package it.polimi.se2.clup.auth;

import it.polimi.se2.clup.data.UserDataAccessInt;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class AuthManager {
    @EJB private UserDataAccessInt userDAO;


    public enum RequestType {

    }

    public String authenticate(String username, String password) {
        return "";
    }

    public String createDailyToken() {
        return "ciao";
    }





}
