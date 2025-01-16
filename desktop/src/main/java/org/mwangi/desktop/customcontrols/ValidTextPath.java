package org.mwangi.desktop.customcontrols;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.util.function.Predicate;

import static atlantafx.base.theme.Styles.STATE_DANGER;

public class ValidTextPath extends TextField {
    private final SimpleBooleanProperty isValidProperty=new SimpleBooleanProperty();
    public ValidTextPath(Predicate<String> val){
                 textProperty().addListener((_,_,newValue) ->{
                     boolean test=val.test(newValue);
                     if(test){
                         pseudoClassStateChanged(STATE_DANGER,false);
                         isValidProperty.set(true);
                     }else{
                         pseudoClassStateChanged(STATE_DANGER,true);
                         Tooltip tooltip = new Tooltip("Not Valid");
                         tooltip.setShowDelay(Duration.millis(100));
                         tooltipProperty().set(tooltip);
                         isValidProperty.set(false);
                     }
                 });

    }
}
