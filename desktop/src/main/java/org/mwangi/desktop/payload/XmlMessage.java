package org.example.findingout;

import jakarta.xml.bind.annotation.XmlAttribute;

import jakarta.xml.bind.annotation.XmlValue;

public class XmlMessage {

    private String name;
    private String content;

    public XmlMessage() {
    }

    public XmlMessage(String name, String content) {
        this.name = name;
        this.content = content;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlValue
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "XmlMessage{name='" + name + "', content='" + content + "'}";
    }
}
