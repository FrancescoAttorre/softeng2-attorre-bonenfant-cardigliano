package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.InvalidDepartmentException;
import it.polimi.se2.clup.data.entities.Department;
import it.polimi.se2.clup.data.entities.LineUpDigitalTicket;
import it.polimi.se2.clup.ticket.NotInQueueException;

import javax.ejb.Local;
import java.time.Duration;
import java.util.List;

/**
 * Interface implemented by BuildingManager and used also by TicketManager to compute updated waiting times for a given
 * line up ticket
 */
@Local
public interface WaitingTimeInt  {

    Duration computeWaitingTime (LineUpDigitalTicket ticket) throws NotInQueueException;

    List<Department> checkDepartments (int buildingID, List<Integer> departments) throws InvalidDepartmentException;
}
