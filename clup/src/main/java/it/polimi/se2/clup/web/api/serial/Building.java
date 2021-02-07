package it.polimi.se2.clup.web.api.serial;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Building {
    @XmlElement public int id;
    @XmlElement public String name;
    @XmlElement public int waitingTime;
}
