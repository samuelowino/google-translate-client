package org.mwangi.desktop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MobileTranslateApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage)  {
             stage.setTitle("kwa ceiling");
             stage.setScene(new Scene(new Label("Hello world!")));
             stage.setMinHeight(100);
             stage.setMinWidth(60);
             stage.show();
    }
}
