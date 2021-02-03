package it.polimi.se2.clup.auth;

import it.polimi.se2.clup.auth.exceptions.CredentialsException;
import it.polimi.se2.clup.data.UserDataAccessInt;
import it.polimi.se2.clup.data.entities.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
public class AuthManagerTest {
    static AuthManager am;
    static UserDataAccessInt userDAO;


    @BeforeAll
    public static void setup() {

        am = new AuthManager();

        userDAO = Mockito.mock(UserDataAccessInt.class);

        am.userDAO = userDAO;

        RegisteredAppCustomer rac = new RegisteredAppCustomer();
        rac.setPassword(Hash.hash("password"));
        rac.setUsername("user");
        rac.setId(1);

        StoreManager sm = new StoreManager();
        sm.setId(2);

        Mockito.when(userDAO.retrieveUser("user")).thenReturn(rac);
        Mockito.when(userDAO.retrieveStoreManager(2)).thenReturn(sm);

        Activity activity = new Activity();
        activity.setId(3);
        activity.setName("Esselunga");
        activity.setPassword(Hash.hash("password"));
        activity.setpIva("AAAAAAAAAAAAAAAA");

        Mockito.when(userDAO.retrieveActivity("AAAAAAAAAAAAAAAA")).thenReturn(activity);

        Mockito.when(userDAO.insertUnregisteredAppCustomer()).thenReturn(3);

    }

    private void removeAllFromDatabase(EntityManager em) {

        //here with mockito this is not needed
        //before each on mockito does not work .........

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
        em.getTransaction().commit();
    }

    @Test
    public void wrongCredentials() {
        Assertions.assertThrows(CredentialsException.class, () -> am.authenticate("user", "wrongPassword"));

        Assertions.assertThrows(CredentialsException.class, () -> am.authenticate("asd", "wrongPassword"));
    }


}
