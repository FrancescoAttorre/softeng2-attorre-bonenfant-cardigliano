package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.*;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.time.LocalDateTime;

@Stateless
public class UserDataAccessImpl implements UserDataAccessInt{

    @PersistenceContext(unitName = "clup")
    protected EntityManager em;

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

    @Override
    public void insertUser(String username, String password) throws EntityExistsException {
        RegisteredAppCustomer appCustomer = new RegisteredAppCustomer();
        appCustomer.setUsername(username);
        appCustomer.setPassword(password);

        em.persist(appCustomer);
    }



    @Override
    public void insertActivity(String name, String pIva, String password) throws EntityExistsException{
        Activity activity = new Activity();
        activity.setName(name);
        activity.setpIva(pIva);
        activity.setPassword(password);

        em.persist(activity);
    }

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

    @Override
    public Activity retrieveActivity(String pIva) throws NonUniqueResultException, NoResultException {

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

    @Override
    public StoreManager retrieveStoreManager(int id) {
        return null;
    }

    @Override
    public UnregisteredAppCustomer retrieveUnregisteredAppCustomer(int userId) {
        return em.find(UnregisteredAppCustomer.class,userId);
    }

    @Override
    public int insertUnregisteredAppCustomer() {
        UnregisteredAppCustomer customer = new UnregisteredAppCustomer();
        customer.setDate(LocalDateTime.now());
        em.persist(customer);

        return customer.getId();
    }
}
