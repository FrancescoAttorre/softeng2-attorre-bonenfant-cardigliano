package it.polimi.se2.clup.web.api.serial;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DepartmentJSON {
    @XmlElement public int departmentID;
    @XmlElement public String name;
}
