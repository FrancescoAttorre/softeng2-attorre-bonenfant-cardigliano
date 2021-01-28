package it.polimi.se2.clup.data;


import it.polimi.se2.clup.data.entities.Activity;
import it.polimi.se2.clup.data.entities.RegisteredAppCustomer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import javax.persistence.*;

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

        dm.insertUser("firstUser", "firstPassword");

        dm.em.getTransaction().commit();

        RegisteredAppCustomer registeredAppCustomer = dm.em.createNamedQuery("RegisteredAppCustomer.checkCredentials", RegisteredAppCustomer.class)
                .setParameter("username", "firstUser").setParameter("password","firstPassword").getSingleResult();

        Assertions.assertEquals("firstUser",registeredAppCustomer.getUsername());
        Assertions.assertEquals("firstPassword",registeredAppCustomer.getPassword());

    }

}