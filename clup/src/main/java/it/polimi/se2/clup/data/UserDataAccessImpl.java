package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.Activity;
import it.polimi.se2.clup.data.entities.RegisteredAppCustomer;
import it.polimi.se2.clup.data.entities.UnregisteredAppCustomer;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Stateless
public class UserDataAccessImpl implements UserDataAccessInt{

    @PersistenceContext(unitName = "clup")
    protected EntityManager em;

    @Override
    public RegisteredAppCustomer retrieveUser(String username, String password) throws NonUniqueResultException, NoResultException{
        return  em.createNamedQuery("RegisteredAppCustomer.checkCredentials", RegisteredAppCustomer.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult();
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
    public Activity retrieveUser(String pIva) throws NonUniqueResultException, NoResultException {
        return  em.createNamedQuery("Activity.selectWithPIVA", Activity.class)
                .setParameter("pIva", pIva)
                .getSingleResult();
    }

    @Override
    public UnregisteredAppCustomer retrieveUnregisteredAppCustomer(int userId) {
        return em.find(UnregisteredAppCustomer.class,userId);
    }

    @Override
    public void insertUnregisteredAppCustomer() {
        UnregisteredAppCustomer customer = new UnregisteredAppCustomer();
        customer.setDate(new Date());
        em.persist(customer);
    }
}
