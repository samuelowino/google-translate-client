package org.mwangi.desktop.customcontrols;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ProgressBar;
import org.mwangi.desktop.util.EventBus;

public class ActivityIndicator extends ProgressBar {
    public ActivityIndicator(EventBus eventBus){
        visibleProperty().bind(Bindings.greaterThan(eventBus.activityCountProperty(),0));
    }
}
