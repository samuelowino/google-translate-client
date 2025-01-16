package org.mwangi.desktop;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Launcher {

    public static void main(String[] args) {

        try {
            LogManager.getLogManager().readConfiguration(Launcher.class.getClassLoader().getResourceAsStream("logging.properties"));
            System.out.println("I have read the properties");
        } catch (IOException e) {
            System.out.println("Logging not configured");
        }

        MobileTranslateApp.main(args);
    }
}
