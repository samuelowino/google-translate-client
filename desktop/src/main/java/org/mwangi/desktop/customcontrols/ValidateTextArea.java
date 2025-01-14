package org.mwangi.desktop.customcontrols;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.util.function.Predicate;

import static atlantafx.base.theme.Styles.STATE_DANGER;

public class ValidateTextArea extends TextArea {
    private  final BooleanProperty isValidProperty=new SimpleBooleanProperty();
    public ValidateTextArea(Predicate<String> validationPredicatesWithMessages){
        textProperty().addListener((_,_,newvalue) -> {

         Object test=validationPredicatesWithMessages.test(newvalue)?null: "Required";
            if(test != null){
                pseudoClassStateChanged(STATE_DANGER,true);
                Tooltip tooltip = new Tooltip(test.toString());
                tooltip.setShowDelay(Duration.millis(100));
                tooltipProperty().set(tooltip);
                isValidProperty.set(false);
            }else{
                pseudoClassStateChanged(STATE_DANGER,false);
                tooltipProperty().set(null);
                isValidProperty.set(true);
            }
        });
    }

    public BooleanProperty isValidPropertyProperty() {
        return isValidProperty;
    }



}
