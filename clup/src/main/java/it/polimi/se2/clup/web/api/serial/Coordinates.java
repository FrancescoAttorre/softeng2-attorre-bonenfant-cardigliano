package it.polimi.se2.clup.web.api.serial;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Coordinates {
    @XmlElement public double latitude;
    @XmlElement public double longitude;
}
