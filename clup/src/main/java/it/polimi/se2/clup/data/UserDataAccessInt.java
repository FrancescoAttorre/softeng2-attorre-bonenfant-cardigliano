package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.Activity;
import it.polimi.se2.clup.data.entities.RegisteredAppCustomer;
import it.polimi.se2.clup.data.entities.StoreManager;
import it.polimi.se2.clup.data.entities.UnregisteredAppCustomer;


public interface UserDataAccessInt {
    RegisteredAppCustomer retrieveUser(String username);
    boolean insertUser(String username, String password);
    boolean insertActivity(String name, String pIva, String password);
    Integer insertStoreManager(String accessCode);
    Activity retrieveActivity(String pIva);
    StoreManager retrieveStoreManager(int id);
    Integer insertUnregisteredAppCustomer();
}
