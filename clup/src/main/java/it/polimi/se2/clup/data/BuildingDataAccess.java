package it.polimi.se2.clup.data;

import it.polimi.se2.clup.data.entities.*;

import javax.persistence.*;
import java.util.List;

public class BuildingDataAccess implements BuildingDataAccessInterface{

    @PersistenceContext(unitName = "clup")
    protected EntityManager em;

    @Override
    public List<Building> retrieveBuildings() {
        return em.createNamedQuery("Building.findAll", Building.class).getResultList();
    }

    @Override
    public BuildingState retrieveBuildingState(int id) throws NoResultException,NonUniqueResultException{
        return em.createNamedQuery("Building.retrieveBuildingById", Building.class)
                .setParameter("buildingId",id)
                .getSingleResult().getState();

    }

    @Override
    public void insertInQueue(LineUpDigitalTicket ticket) throws NoResultException {
        Queue queue = em.find(Queue.class, ticket.getBuilding().getBuildingID());
        queue.addQueueTickets(ticket);
        em.persist(queue);
    }
}
