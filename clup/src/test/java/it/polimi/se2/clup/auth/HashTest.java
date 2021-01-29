package it.polimi.se2.clup.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HashTest {
    @Test
    public void hash() {
        String password = "mypassword";

        String hashedPassword = Hash.hash(password);
        String hashedPassword2 = Hash.hash(password);

        System.out.println(hashedPassword + "\n" + hashedPassword2);

        Assertions.assertNotEquals(hashedPassword2, hashedPassword);
        Assertions.assertTrue(Hash.verifyHash(password, hashedPassword));
        Assertions.assertTrue(Hash.verifyHash(password, hashedPassword2));

    }
}
