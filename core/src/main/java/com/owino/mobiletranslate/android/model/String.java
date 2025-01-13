package com.owino.mobiletranslate.android.model;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 */

public class String {

    private java.lang.String name;

    private java.lang.String content;

    public String() {
    }

    public String(java.lang.String name, java.lang.String content) {
        this.name = name;
        this.content = content;
    }

    @XmlAttribute
    public java.lang.String getName() {
        return name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }

    @XmlValue
    public java.lang.String getContent() {
        return content;
    }

    public void setContent(java.lang.String content) {
        this.content = content;
    }

    @Override
    public java.lang.String toString() {
        return "String{" +
                "name='" + name +'\'' +
                ", content='" + content +'\'' +
               '}';
    }
}
