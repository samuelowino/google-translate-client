package org.mwangi.desktop;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.mwangi.desktop.mainview.MainView;
import org.mwangi.desktop.util.FxExecutor;
import org.mwangi.desktop.util.UI;

import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class MobileTranslateApp extends Application {
    static Logger log=Logger.getLogger(Launcher.class.getSimpleName());
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage){
        log.info("APP HAS STARTED");
        System.setProperty("prism.lcdtext", "false");
       AppFactory appFactory=DaggerAppFactory.builder().build();
       appFactory.provideThemeManager().applyCurrentTheme();
        stage.setTitle("Mobile Translate");
        stage.getIcons().add(new Image(getClass().getResource("/icons/home_icon.png").toString()));
        stage.setScene(
                UI.scene(
                        ()->{

                            Region mainview=appFactory.mainView();
                            mainview.setPrefHeight(600);
                            mainview.setPrefWidth(700);
                            mainview.setMaxWidth(800);
                            mainview.setMaxHeight(600);
                            return mainview;
                        }
                )
        );
        stage.getScene().getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        stage.setResizable(false);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Executor executor= FxExecutor.getInstance();
                executor.execute(() -> MainView.previewStage.close());
                Platform.exit();
                System.exit(0);
            }
        });
        stage.show();
    }

}
