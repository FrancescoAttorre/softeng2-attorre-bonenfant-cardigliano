package it.polimi.se2.clup.data;

import javax.ejb.Local;

@Local
public interface UserDataAccessInt {
    void retrieveUser(String username, String password);
    void insertAccess(String token); //TODO rivedere
    void insertUser(String username, String password);
    void insertActivity(String name, String pIva, String password);
    void retrieveUser(String pIva);
}
