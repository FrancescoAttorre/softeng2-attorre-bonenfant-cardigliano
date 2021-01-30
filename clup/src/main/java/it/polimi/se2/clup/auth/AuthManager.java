package it.polimi.se2.clup.auth;

import it.polimi.se2.clup.auth.exceptions.CredentialsException;
import it.polimi.se2.clup.auth.exceptions.TokenException;
import it.polimi.se2.clup.data.UserDataAccessInt;
import it.polimi.se2.clup.data.entities.RegisteredAppCustomer;
import it.polimi.se2.clup.data.entities.StoreManager;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class AuthManager implements AuthManagerInt{
    @EJB private UserDataAccessInt userDAO;

    //TODO: Handle specific exception and not general Exception type

    TokenManager tokenManager = new TokenManager();

    private final long regExpiration = 60000; //1m
    private final long unregExpiration = 1440000; //24h

    @Override
    public boolean registerUser(String username, String password) {
        boolean result = true;

        if(username != null && password != null) {

            String hashedPassword = Hash.hash(password);

            try {
                userDAO.insertUser(username, hashedPassword);
            } catch (Exception e) {
                result = false;
            }
        } else
            result = false;

        return result;
    }

    @Override
    public boolean registerActivity(String name, String pIVA, String password) {
        boolean result = true;

        if(name != null && pIVA != null && password != null) {

            String hashedPassword = Hash.hash(password);

            try {
                userDAO.insertActivity(name, pIVA, hashedPassword);
            } catch (Exception e) {
                result = false;
            }
        } else
            result = false;

        return result;
    }

    @Override
    public String authenticate(int storeManagerID) {
        StoreManager sm = userDAO.retrieveStoreManager(storeManagerID);

        return tokenManager.createToken(storeManagerID, regExpiration, AuthFlag.MANAGER);
    }

    public String authenticate(String username, String password) throws CredentialsException {
        RegisteredAppCustomer rac = userDAO.retrieveUser(username);

        String hashedPassword = rac.getPassword();
        String token;

        if(Hash.verifyHash(password, hashedPassword)) {
            token = tokenManager.createToken(rac.getId(), regExpiration, AuthFlag.REGISTERED);
        } else
            throw new CredentialsException("Wrong credentials");

        return token;

    }

    public String createDailyToken() {
        int id = userDAO.insertUnregisteredAppCustomer();

        return tokenManager.createToken(id, unregExpiration, AuthFlag.UNREGISTERED);
    }

    @Override
    public int verifyToken(String token, AuthFlag auth) throws TokenException {
        return tokenManager.verify(token, auth);
    }


}
