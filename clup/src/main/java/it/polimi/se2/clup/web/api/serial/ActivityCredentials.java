package it.polimi.se2.clup.web.api.serial;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ActivityCredentials  {
    @XmlElement(required = true) public String name;
    @XmlElement(required = true) public String pIVA;
    @XmlElement(required = true) public String password;

}
