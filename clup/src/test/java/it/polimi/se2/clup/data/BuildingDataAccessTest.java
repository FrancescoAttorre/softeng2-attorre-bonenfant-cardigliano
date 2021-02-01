package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.Building;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class BuildingDataAccessTest {

    private static BuildingDataAccess dm;

    @BeforeAll
    public static void setup() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("clupTest");
        dm = new BuildingDataAccess();
        dm.em = emf.createEntityManager();

        dm.em.getTransaction().begin();

        Query q1 = dm.em.createQuery("delete from Building");
        Query q2 = dm.em.createQuery("delete from Department");
        q1.executeUpdate();
        q2.executeUpdate();

        //creation of EsselungaStore

        Map<String,Integer> surplus = new HashMap<>();
        surplus.put("Macelleria",10);
        surplus.put("Pescheria",3);
        surplus.put("Bevande",12);

        dm.insertBuilding(
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
        int oldLastExitTime = building.getLastExitTime();

        dm.updateStatistics(building.getBuildingID(),exitTime);

        dm.em.getTransaction().commit();

        Duration newDelta = building.getDeltaExitTime();
        int newLastExitTime = building.getLastExitTime();

        //TODO:
        Assertions.assertNotEquals(oldDelta,newDelta);

    }
}
