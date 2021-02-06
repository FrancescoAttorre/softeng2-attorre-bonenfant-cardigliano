package it.polimi.se2.clup.auth;

import it.polimi.se2.clup.auth.exceptions.CredentialsException;
import it.polimi.se2.clup.auth.exceptions.TokenException;
import it.polimi.se2.clup.data.UserDataAccessInt;
import it.polimi.se2.clup.data.entities.Activity;
import it.polimi.se2.clup.data.entities.RegisteredAppCustomer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class AuthManager implements AuthManagerInt{

    @EJB protected UserDataAccessInt userDAO;

    TokenManager tokenManager = new TokenManager();

    private final long regExpiration = 60 * 1000; //1m
    private final long unregExpiration = 1000 * 60 * 60 * 24; //24h
    private final long activityExpiration = 30 * 60 * 1000; //30m

    @Override
    public boolean registerUser(String username, String password) {
        boolean result;

        if(username != null && password != null) {

            String hashedPassword = Hash.hash(password);

            result = userDAO.insertUser(username, hashedPassword);

        } else
            result = false;

        return result;
    }

    @Override
    public boolean registerActivity(String name, String pIVA, String password) {
        boolean result;

        if(name != null && pIVA != null && password != null) {

            String hashedPassword = Hash.hash(password);

            result = userDAO.insertActivity(name, pIVA, hashedPassword);

        } else
            result = false;

        return result;
    }

    @Override
    public String authenticate(String buildingAccessCode) throws CredentialsException{
        Integer id = userDAO.insertStoreManager(Hash.simpleHash(buildingAccessCode));

        if(id == null)
            throw new CredentialsException("Wrong Access Code");

        return tokenManager.createToken(id, regExpiration, AuthFlag.MANAGER);
    }

    public String authenticate(String username, String password) throws CredentialsException {
        RegisteredAppCustomer rac = userDAO.retrieveUser(username);

        if(rac == null) {
            throw new CredentialsException("Username does not exist");
        }

        String hashedPassword = rac.getPassword();
        String token;

        if(Hash.verifyHash(password, hashedPassword)) {
            token = tokenManager.createToken(rac.getId(), regExpiration, AuthFlag.REGISTERED);
        } else
            throw new CredentialsException("Wrong password");

        return token;

    }

    @Override
    public String authenticateActivity(String pIVA, String password) throws CredentialsException {
        Activity activity = userDAO.retrieveActivity(pIVA);

        if(activity == null)
            throw new CredentialsException("Activity does not exist");

        String hashedPassword = activity.getPassword();
        String token;

        if(Hash.verifyHash(password, hashedPassword)) {
            token = tokenManager.createToken(activity.getId(), activityExpiration, AuthFlag.ACTIVITY);
        } else
            throw new CredentialsException("Wrong credentials");

        return token;
    }

    public String createDailyToken() {
        int id = userDAO.insertUnregisteredAppCustomer();

        return tokenManager.createToken(id, unregExpiration, AuthFlag.UNREGISTERED);
    }

    @Override
    public int verifyToken(String token, List<AuthFlag> authList) throws TokenException {
        return tokenManager.verify(token, authList);
    }

    @Override
    public AuthFlag getAuthFlag(String token) {
        return tokenManager.getAuthFlag(token);
    }

    @Override
    public String[] generateAccessCode() {
        String accessCode = AccessCode.generate();

        return new String[]{accessCode, Hash.simpleHash(accessCode)};
    }

    public UserDataAccessInt getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDataAccessInt userDAO) {
        this.userDAO = userDAO;
    }
}
