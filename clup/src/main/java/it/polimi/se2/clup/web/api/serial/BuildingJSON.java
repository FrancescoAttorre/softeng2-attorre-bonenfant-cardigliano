package it.polimi.se2.clup.web.api.serial;

import it.polimi.se2.clup.data.entities.Department;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class BuildingJSON {
    @XmlElement public int id;
    @XmlElement public String name;
    @XmlElement public int waitingTime;
    @XmlList public List<DepartmentJSON> departments;
}
