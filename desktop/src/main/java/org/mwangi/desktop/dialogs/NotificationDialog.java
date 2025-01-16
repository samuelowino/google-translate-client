package org.mwangi.desktop.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class NotificationDialog extends Alert {
    public NotificationDialog(String message){
        this(message,null);
    }


    public NotificationDialog(String message, String description){
        super(AlertType.ERROR);
        ((Stage) this.getDialogPane().getScene().getWindow()).getIcons().add(new Image(getClass().getResource("/icons/notification_icon.png").toString()));
        setResizable(true);
        setTitle("Notification");
        setHeaderText(message);
        setContentText(description ==null?"Something went wrong":description);

    }
}
