package org.mwangi.desktop;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Launcher {
    static Logger log=Logger.getLogger(Launcher.class.getSimpleName());
    public static void main(String[] args) {

        try {
            LogManager.getLogManager().readConfiguration(Launcher.class.getClassLoader().getResourceAsStream("logging.properties"));
        } catch (IOException e) {
            System.out.println("Logging not configured");
        }
        log.info("APP HAS STARTED");
        MobileTranslateApp.main(args);
    }
}
