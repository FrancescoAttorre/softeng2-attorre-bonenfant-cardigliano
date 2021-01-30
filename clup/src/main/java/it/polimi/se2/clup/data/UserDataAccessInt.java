package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.Activity;
import it.polimi.se2.clup.data.entities.RegisteredAppCustomer;
import it.polimi.se2.clup.data.entities.StoreManager;
import it.polimi.se2.clup.data.entities.UnregisteredAppCustomer;

import javax.ejb.Local;

@Local
public interface UserDataAccessInt {
    RegisteredAppCustomer retrieveUser(String username);
    void insertUser(String username, String password);
    void insertActivity(String name, String pIva, String password);
    Activity retrieveActivity(String pIva);
    StoreManager retrieveStoreManager(int id);
    UnregisteredAppCustomer retrieveUnregisteredAppCustomer(int userId);
    int insertUnregisteredAppCustomer();
}
