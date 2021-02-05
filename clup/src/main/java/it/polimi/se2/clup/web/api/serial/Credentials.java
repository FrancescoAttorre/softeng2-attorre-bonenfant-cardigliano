package it.polimi.se2.clup.web.api.serial;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Credentials {
    @XmlElement(required = true) public String username;
    @XmlElement(required = true) public String password;
}
