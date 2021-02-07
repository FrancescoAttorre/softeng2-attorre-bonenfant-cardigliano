package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.*;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * UserDataAcces component has to has to add/retrieve to/from the database tickets and the information related to them,
 * information necessary to the TicketManager
 */
@Stateless
public class UserDataAccess implements UserDataAccessInt {

    //public for test purposes
    @PersistenceContext(unitName = "clup")
    public EntityManager em;

    /**
     * Retrieves a registered app customer from the database, given its username as parameter
     * @return registered app customer
     */
    @Override
    public RegisteredAppCustomer retrieveUser(String username) {

        RegisteredAppCustomer rac;

        try {
            rac = em.createNamedQuery("RegisteredAppCustomer.findUserByUsername", RegisteredAppCustomer.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            rac = null;
        }

        return rac;
    }

    /**
     * Creates a new registered app customer, given username and password, and makes it persist in the database
     * @return true if the insertion was successful
     */
    @Override
    public boolean insertUser(String username, String password) {

        boolean result = true;

        System.out.println("Adding user " + username);

        if (username == null || password == null)
            return false;

        if(em.createNamedQuery("RegisteredAppCustomer.findUserByUsername").setParameter("username", username).getResultList().size() > 0)
            result = false;
        else {

            System.out.println("Persisting user " + username);

            RegisteredAppCustomer appCustomer = new RegisteredAppCustomer();
            appCustomer.setUsername(username);
            appCustomer.setPassword(password);

            em.persist(appCustomer);
        }

        return result;
    }


    /**
     * Creates a new activity, given its name, pIva and password, and makes it persist in the database
     * @return true if the insertion was successful
     */
    @Override
    public boolean insertActivity(String name, String pIva, String password) {
        boolean result = true;

        if(em.createNamedQuery("Activity.selectWithPIVA").setParameter("pIva", pIva).getResultList().size() > 0)
            result = false;
        else if (em.createNamedQuery("Activity.selectWithName").setParameter("name", pIva).getResultList().size() > 0){
            result = false;
        } else {
            Activity activity = new Activity();
            activity.setName(name);
            activity.setpIva(pIva);
            activity.setPassword(password);

            em.persist(activity);
        }

        return result;
    }

    /**
     * Creates a new store manager, given the accessCode of the building he's related to, and makes it persist in the database
     * @return true if the insertion was successful
     */
    @Override
    public Integer insertStoreManager(String accessCode) {
        StoreManager sm = new StoreManager();

        Integer id;

        try {
            Building b = em.createNamedQuery("Building.retrieveByAccessCode", Building.class)
                    .setParameter("accessCode", accessCode)
                    .getSingleResult();
            sm.setBuilding(b);
            em.persist(sm);

            id = sm.getId();
        } catch (NoResultException e) {
            id = null;
        }

        return id;
    }

    /**
     * Retrieves an activity give its pIva as parameter
     * @throws NonUniqueResultException thrown if it has been found more than an activity with the same pIva
     */
    @Override
    public Activity retrieveActivity(String pIva) throws NonUniqueResultException {

        Activity activity;

        try {
            activity = em.createNamedQuery("Activity.selectWithPIVA", Activity.class)
                    .setParameter("pIva", pIva)
                    .getSingleResult();
        } catch (NoResultException e) {
            activity = null;
        }

        return  activity;
    }

    /**
     * @return a store manager given as parameter his id
     */
    @Override
    public StoreManager retrieveStoreManager(int id) {
        return em.find(StoreManager.class, id);
    }

    /**
     * Creates a new unregistered app customer, and makes it persist in the database
     * @return the id of the new unregistered app customer
     */
    @Override
    public Integer insertUnregisteredAppCustomer() {

        Integer result;

        UnregisteredAppCustomer customer = new UnregisteredAppCustomer();
        customer.setDate(LocalDateTime.now());
        try {
            em.persist(customer);
            result = customer.getId();
        } catch (Exception e) {
            result = null;
        }

        return result;
    }
}
