package org.mwangi.desktop.properties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.prefs.Preferences;

public class PersistentProperties {
    final Preferences prefs;
    final List<Runnable> onSave=new ArrayList<>(2);
    final List<Runnable> onReset=new ArrayList<>(2);
    public PersistentProperties(){
        prefs=Preferences.userNodeForPackage(this.getClass());
    }

    public PersistentProperties(Class<?> clazz){
        prefs=Preferences.userNodeForPackage(Objects.requireNonNull(clazz));
    }
    public  StringProperty getString(String name){
        return getString(name,"");
    }
    public StringProperty getString(String name,String defValue){
        var prop=new SimpleStringProperty(prefs.get(name,defValue));
        onReset.add(()->prop.set(defValue));
        onSave.add(()->prefs.put(name,prop.getValue()));
        return prop;
    }
    public void reset(){
        onReset.forEach(Runnable::run);
    }
    public  void save(){
        onSave.forEach(Runnable::run);
    }
}
