package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.Activity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class DataManager implements UserDataAccessInt{

    @PersistenceContext(unitName = "clup")
    protected EntityManager em;

    @Override
    public void retrieveUser(String username, String password) {

    }

    @Override
    public void insertAccess(String token) {

    }

    @Override
    public void insertUser(String username, String password) {

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
