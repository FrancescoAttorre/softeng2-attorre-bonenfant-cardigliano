package it.polimi.se2.clup.web.api.serial;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TicketJSON {
    @XmlElement public int id;
    @XmlElement public int userID;
    @XmlElement public int buildingID;
    @XmlElement public int waitingTime;
}
