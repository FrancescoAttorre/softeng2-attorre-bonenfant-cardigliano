package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.Activity;
import it.polimi.se2.clup.data.entities.RegisteredAppCustomer;

import javax.ejb.Local;

@Local
public interface UserDataAccessInt {
    RegisteredAppCustomer retrieveUser(String username, String password);
    void insertUser(String username, String password);
    void insertActivity(String name, String pIva, String password);
    Activity retrieveUser(String pIva);
    //TODO manca retrieveActivity nel DD
}
