package it.polimi.se2.clup.data;


import it.polimi.se2.clup.data.entities.Activity;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class DataManagerTest {

    private static DataManager dm;

    @BeforeAll
    public static void setup() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("clupTest");
        dm = new DataManager();
        dm.em = emf.createEntityManager();

        dm.em.getTransaction().begin();

        System.out.println("Deleting table Activity");
        Query q1 = dm.em.createQuery("delete from Activity");
        q1.executeUpdate();
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





}
