package it.polimi.se2.clup.data.entities;

import javax.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(name = "RegisteredAppCustomer.checkCredentials", query = "SELECT u FROM RegisteredAppCustomer u WHERE u.username = :username AND u.password = :password"),
        @NamedQuery(name = "RegisteredAppCustomer.findUserByUsername", query = "SELECT u FROM RegisteredAppCustomer u WHERE u.username = :username"),
})
public class RegisteredAppCustomer extends AppCustomer {

    @Column(unique = true, nullable = false)
    private String username;

    @Column
    private String password;

    public RegisteredAppCustomer() {
        super();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
