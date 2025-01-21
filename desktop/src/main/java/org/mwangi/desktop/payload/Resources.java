package org.mwangi.desktop.payload;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement
public class Resources {

    private List<XmlMessage> strings;

    @XmlElement(name = "string")
    public List<XmlMessage> getStrings() {
        return strings;
    }

    public void setStrings(List<XmlMessage> strings) {
        this.strings = strings;
    }

    @Override
    public String toString() {
        return "Resources{" +
                "strings=" + strings +
                '}';
    }
}
