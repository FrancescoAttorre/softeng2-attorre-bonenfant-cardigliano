package it.polimi.se2.clup.ticket;

import it.polimi.se2.clup.building.BuildingManager;
import it.polimi.se2.clup.data.TicketDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;

public class TicketManagetTest {
    static TicketManager tm;
    static TicketDataAccess tda;
    static BuildingManager bm;

    @BeforeAll
    public static void setup() {

        tm = new TicketManager();

        tda = Mockito.mock(TicketDataAccess.class);
        bm = Mockito.mock(BuildingManager.class);

        tm.setTicketDataAccess(tda);
        tm.setBuildingManager(bm);
    }

    //TODO: test of waiting times

}
