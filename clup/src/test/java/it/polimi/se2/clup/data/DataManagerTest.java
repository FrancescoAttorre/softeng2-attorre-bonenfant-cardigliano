package it.polimi.se2.clup.data;


import it.polimi.se2.clup.data.entities.Activity;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class DataManagerTest {

    private static DataManager dm;

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void setup() {

        emf = Persistence.createEntityManagerFactory("clupTest");
        dm = new DataManager();
        dm.em = emf.createEntityManager();

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
