package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.Building;
import it.polimi.se2.clup.data.entities.BuildingState;
import it.polimi.se2.clup.data.entities.DigitalTicket;
import it.polimi.se2.clup.data.entities.Queue;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

public class BuildingDataAccess implements BuildingDataAccessInterface{

    @PersistenceContext(unitName = "clup")
    protected EntityManager em;

    @Override
    public List<Building> retrieveBuildings() {
        return em.createNamedQuery("Building.findAll", Building.class).getResultList();
    }

    @Override
    public BuildingState retrieveBuildingState(int id) {
        List<Building> buildings = em.createNamedQuery("Building.retrieveBuildingById", Building.class)
                .setParameter("buildingId",id)
                .getResultList();
        if(buildings.isEmpty())
            return null;
        else if (buildings.size() == 1)
            return buildings.get(0).getState();
        else
            throw new NonUniqueResultException("More than one Building with same id");

    }

    @Override
    public void insertInQueue(DigitalTicket ticket) {
        /*
        List<Queue> queues = em.createNamedQuery("Queue.selectQueueWithBuildingId",Queue.class)
                .setParameter(ticket.get).getResultList();
        if(queues.isEmpty())
            throw new EntityExistsException();

         */
    }
}
