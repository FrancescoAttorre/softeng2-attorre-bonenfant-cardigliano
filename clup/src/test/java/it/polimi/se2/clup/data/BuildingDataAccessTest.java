package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class BuildingDataAccessTest {

    private static BuildingDataAccess dm;

    int activityId;

    @BeforeEach
    public void setup() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("clupTest");
        dm = new BuildingDataAccess();
        dm.em = emf.createEntityManager();

        removeAllFromDatabase(dm.em);

        dm.em.getTransaction().begin();

        //creation of EsselungaStore
        UserDataAccessImpl userDataAccess  = new UserDataAccessImpl();
        userDataAccess.em = dm.em;

        userDataAccess.insertActivity("EsselungaActivity","PIVAEsselunga","EsselungaPassword");
        activityId = userDataAccess.retrieveActivity("PIVAEsselunga").getId();

        Map<String,Integer> surplus = new HashMap<>();
        surplus.put("Macelleria",10);
        surplus.put("Pescheria",3);
        surplus.put("Bevande",12);

        dm.insertBuilding(
                activityId,
                "EsselungaStore",
                LocalTime.of(8,0,0),
                LocalTime.of(21,0,0),
                "via Roma,1",
                150,
                surplus,
                "EsselungaStoreAccessCode"
                );


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
    public void shouldCreateStatistics(){
        dm.em.getTransaction().begin();



        dm.em.getTransaction().commit();
    }

    @Test
    public void shouldKeepStatisticsUpToDate(){

        dm.em.getTransaction().begin();

        //simulate exit event at time :
        LocalTime exitTime = LocalTime.of(8,20,0);

        Building building = dm.em.createNamedQuery("Building.retrieveBuildingByName", Building.class).setParameter("buildingName","EsselungaStore").getSingleResult();

        Duration oldDelta = building.getDeltaExitTime();
        LocalTime oldLastExitTime = building.getLastExitTime();

        dm.updateStatistics(building.getBuildingID(),exitTime);

        dm.em.getTransaction().commit();

        Duration newDelta = building.getDeltaExitTime();
        LocalTime newLastExitTime = building.getLastExitTime();

        //TODO:
        Assertions.assertNotEquals(oldDelta,newDelta);

    }
}
