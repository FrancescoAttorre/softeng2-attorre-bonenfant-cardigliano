package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.Activity;
import it.polimi.se2.clup.data.entities.RegisteredAppCustomer;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class UserDaraAccessImpl implements UserDataAccessInt{

    @PersistenceContext(unitName = "clup")
    protected EntityManager em;

    @Override
    public RegisteredAppCustomer retrieveUser(String username, String password) {

        return em.createNamedQuery("RegisteredAppCustomer.findUserByUsernameAndPassword", RegisteredAppCustomer.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .getSingleResult();

    }

    @Override
    public void insertAccess(String token) {

    }

    @Override
    public void insertUser(String username, String password) {
        RegisteredAppCustomer appCustomer = new RegisteredAppCustomer();
        appCustomer.setUsername(username);
        appCustomer.setPassword(password);

        List<RegisteredAppCustomer> users;

        users = em.createNamedQuery("RegisteredAppCustomer.findUserByUsername",RegisteredAppCustomer.class)
                .setParameter("username",username)
                .getResultList();

        if(users.isEmpty())
            em.persist(appCustomer);

    }



    @Override
    public void insertActivity(String name, String pIva, String password) {
        Activity activity = new Activity();
        activity.setName(name);
        activity.setpIva(pIva);
        activity.setPassword(password);

        em.persist(activity);
    }

    @Override
    public void retrieveUser(String pIva) {

    }
}
