package it.polimi.se2.clup.data;


import it.polimi.se2.clup.data.entities.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;

import javax.persistence.*;

public class UserDataAccessImplTest {

    private static UserDataAccessImpl dm;

    @BeforeEach
    public void setup() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("clupTest");
        dm = new UserDataAccessImpl();
        dm.em = emf.createEntityManager();

        removeAllFromDatabase(dm.em);

        dm.em.getTransaction().begin();

        dm.insertUser("firstUser", "firstPassword");

        dm.em.getTransaction().commit();

    }

    private void removeAllFromDatabase(EntityManager em) {
        em.getTransaction().begin();
        for(RegisteredAppCustomer u : em.createNamedQuery("RegisteredAppCustomer.findAll",RegisteredAppCustomer.class).getResultList()){
            em.remove(u);
        }
        for(UnregisteredAppCustomer u : em.createNamedQuery("UnregisteredAppCustomer.findAll",UnregisteredAppCustomer.class).getResultList()){
            em.remove(u);
        }
        for(StoreManager u : em.createNamedQuery("StoreManager.findAll",StoreManager.class).getResultList()){
            em.remove(u);
        }
        for(Building b : em.createNamedQuery("Building.findAll",Building.class).getResultList()){
            em.remove(b);
        }
        for(Activity a : em.createNamedQuery("Activity.selectAll",Activity.class).getResultList()){
            em.remove(a);
        }
        em.getTransaction().commit();
    }

    @Test
    public void insertActivity() {

        dm.em.getTransaction().begin();

        dm.insertActivity("Esselunga", "abc123", "abc123");

        dm.em.getTransaction().commit();

        //dm.em.getTransaction().begin();

        Query query = dm.em.createNamedQuery("Activity.selectWithName").setParameter("name", "Esselunga");

        Activity result = (Activity) query.getSingleResult();

        Assertions.assertEquals("Esselunga", result.getName());

    }


    @Test
    public void insertRegisteredAppCustomer() {

        dm.em.getTransaction().begin();

        dm.insertUser("insertedUser", "insertedPassword");

        dm.em.getTransaction().commit();

        RegisteredAppCustomer registeredAppCustomer = dm.em.createNamedQuery("RegisteredAppCustomer.findUserByUsername", RegisteredAppCustomer.class)
                .setParameter("username", "insertedUser").getSingleResult();

        Assertions.assertEquals("insertedUser",registeredAppCustomer.getUsername());
        Assertions.assertEquals("insertedPassword",registeredAppCustomer.getPassword());

    }

    @Test
    public void insertUnregisteredAppCustomer() {

        dm.em.getTransaction().begin();

        dm.insertUnregisteredAppCustomer();

        dm.em.getTransaction().commit();

        Assertions.assertDoesNotThrow(() -> dm.em.createNamedQuery("UnregisteredAppCustomer.findAll", UnregisteredAppCustomer.class)
                .getResultList());


    }

    @Test
    public void retrieveRegisteredAppCustomer() {
        RegisteredAppCustomer registeredAppCustomer = dm.em.createNamedQuery("RegisteredAppCustomer.findUserByUsername", RegisteredAppCustomer.class)
                .setParameter("username", "firstUser").getSingleResult();

        Assertions.assertEquals("firstUser",registeredAppCustomer.getUsername());
        Assertions.assertEquals("firstPassword",registeredAppCustomer.getPassword());

    }

    @Test
    public void unExistentUser() {
        Assertions.assertNull(dm.retrieveUser("unExistentUser"));
    }

    @Test
    public void unExistentActivity() {
        Assertions.assertNull(dm.retrieveActivity("unExistentPIVA"));
    }

}
