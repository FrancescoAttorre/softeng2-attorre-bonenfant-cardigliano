package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.Activity;
import it.polimi.se2.clup.data.entities.RegisteredAppCustomer;

import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class UserDataAccessImpl implements UserDataAccessInt{

    @PersistenceContext(unitName = "clup")
    protected EntityManager em;

    @Override
    public RegisteredAppCustomer retrieveUser(String username, String password) {
        List<RegisteredAppCustomer> customers;

        customers =  em.createNamedQuery("RegisteredAppCustomer.checkCredentials", RegisteredAppCustomer.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getResultList();

        if(customers.isEmpty())
            return null;
        else if (customers.size() == 1)
            return customers.get(0);
        else
            throw new NonUniqueResultException("More than one user registered with same credentials");

    }

    @Override
    public void insertUser(String username, String password) throws EntityExistsException {
        RegisteredAppCustomer appCustomer = new RegisteredAppCustomer();
        appCustomer.setUsername(username);
        appCustomer.setPassword(password);

        try{
            em.persist(appCustomer);
        }catch(EntityExistsException e){
            throw e;
        }
    }



    @Override
    public void insertActivity(String name, String pIva, String password) throws EntityExistsException{
        Activity activity = new Activity();
        activity.setName(name);
        activity.setpIva(pIva);
        activity.setPassword(password);
        try{
            em.persist(activity);
        }catch(EntityExistsException e){
            throw e;
        }
    }

    @Override
    public Activity retrieveUser(String pIva) {
        List<Activity> activities;

        activities =  em.createNamedQuery("Activity.selectWithPIVA", Activity.class)
                .setParameter("pIva", pIva)
                .getResultList();

        if(activities.isEmpty())
            return null;
        else if (activities.size() == 1)
            return activities.get(0);
        else
            throw new NonUniqueResultException("More than one Activity with same pIva");
    }
}
