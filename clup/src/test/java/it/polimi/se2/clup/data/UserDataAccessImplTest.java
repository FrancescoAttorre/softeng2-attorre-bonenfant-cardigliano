package it.polimi.se2.clup.data;


import it.polimi.se2.clup.data.entities.Activity;
import it.polimi.se2.clup.data.entities.RegisteredAppCustomer;
import it.polimi.se2.clup.data.entities.UnregisteredAppCustomer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import javax.persistence.*;
import java.util.List;

public class UserDataAccessImplTest {

    private static UserDataAccessImpl dm;

    @BeforeAll
    public static void setup() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("clupTest");
        dm = new UserDataAccessImpl();
        dm.em = emf.createEntityManager();

        dm.em.getTransaction().begin();

        System.out.println("Deleting table Activity");
        Query q1 = dm.em.createQuery("delete from Activity");
        Query q2 = dm.em.createQuery("delete from RegisteredAppCustomer");
        q1.executeUpdate();
        q2.executeUpdate();

        dm.insertUser("firstUser", "firstPassword");

        dm.em.getTransaction().commit();

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

        RegisteredAppCustomer registeredAppCustomer = dm.em.createNamedQuery("RegisteredAppCustomer.checkCredentials", RegisteredAppCustomer.class)
                .setParameter("username", "insertedUser").setParameter("password","insertedPassword").getSingleResult();

        Assertions.assertEquals("insertedUser",registeredAppCustomer.getUsername());
        Assertions.assertEquals("insertedPassword",registeredAppCustomer.getPassword());

    }

    @Test
    public void insertUnregisteredAppCustomer() {

        dm.em.getTransaction().begin();

        dm.insertUnregisteredAppCustomer();

        dm.em.getTransaction().commit();

        List<UnregisteredAppCustomer> unregisteredAppCustomers = dm.em.createNamedQuery("UnregisteredAppCustomer.selectAll", UnregisteredAppCustomer.class)
                .getResultList();



    }
    @Test
    public void retrieveRegisteredAppCustomer() {
        RegisteredAppCustomer registeredAppCustomer = dm.em.createNamedQuery("RegisteredAppCustomer.findUserByUsername", RegisteredAppCustomer.class)
                .setParameter("username", "firstUser").getSingleResult();

        Assertions.assertEquals("firstUser",registeredAppCustomer.getUsername());
        Assertions.assertEquals("firstPassword",registeredAppCustomer.getPassword());

    }
}
