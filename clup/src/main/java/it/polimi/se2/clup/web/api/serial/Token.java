package it.polimi.se2.clup.web.api.serial;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Token {

    public Token(String token) {
        this.token = token;
    }

    @XmlElement(required = true) public String token;
}
