package it.polimi.se2.clup.web.api.serial;

import it.polimi.se2.clup.data.entities.Department;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.util.List;

@XmlRootElement
public class BookingRequest {
    @XmlElement(required = true) public int buildingID;
    @XmlElement(required = true) public LocalDate date;
    @XmlElement(required = true) public int timeSlotID;
    @XmlElement(required = true) public int timeSlotLength;
    @XmlList public List<Integer> departments;
}
