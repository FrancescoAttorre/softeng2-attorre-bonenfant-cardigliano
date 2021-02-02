package it.polimi.se2.clup.auth;

import it.polimi.se2.clup.auth.exceptions.CredentialsException;
import it.polimi.se2.clup.auth.exceptions.TokenException;


public interface AuthManagerInt {
    /**
     * Registers a user as RegisteredAppCustomer
     * @return True if the User has been correctly registered
     */
    boolean registerUser(String username, String password);

    /**
     * Registers an Activity to the system
     *
     * @return True if the Activity has been correctly registered
     */
    boolean registerActivity(String name, String pIVA, String password);

    /**
     * Returns an Authorization Token for a StoreManager
     * @return Authorization token
     */
    String authenticate(String buildingAccessCode) throws CredentialsException;

    /**
     * Checks credentials and returns an Authorization token

     * @return Authorization token
     * @throws CredentialsException
     */
    String authenticate(String username, String password) throws CredentialsException;

    String authenticateActivity(String pIVA, String password) throws CredentialsException;

    /**
     * Add an UnregisterdAppCustomer to the system and returns an Authorization token
     * @return
     */
    String createDailyToken();

    /**
     * Verifies a token and the Authorization claim it contains
     * @return id contained in the token
     * @throws TokenException
     */
    int verifyToken(String token, AuthFlag auth) throws TokenException;

    /**
     * Generates an Array containing an access code and its hash (no salt)
     * @return {code, hashedCode}
     */
    String[] generateAccessCode();

}