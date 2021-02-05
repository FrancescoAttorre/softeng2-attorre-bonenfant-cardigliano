package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.*;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.time.LocalDateTime;

@Stateless
public class UserDataAccessImpl implements UserDataAccessInt{

    //TODO: set to protected? (public for test pusposes)
    @PersistenceContext(unitName = "clup")
    public EntityManager em;

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
    public boolean insertUser(String username, String password) {

        boolean result = true;

        System.out.println("Adding user " + username);

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



    @Override
    public boolean insertActivity(String name, String pIva, String password) {
        boolean result = true;

        if(em.createNamedQuery("Activity.selectWithPIVA").setParameter("pIva", pIva).getResultList().size() > 0)
            result = false;
        else {
            Activity activity = new Activity();
            activity.setName(name);
            activity.setpIva(pIva);
            activity.setPassword(password);

            em.persist(activity);
        }

        return result;
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

    @Override
    public StoreManager retrieveStoreManager(int id) {
        return em.find(StoreManager.class, id);
    }

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
