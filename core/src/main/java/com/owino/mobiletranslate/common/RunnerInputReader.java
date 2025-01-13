package com.owino.mobiletranslate.common;

import com.owino.mobiletranslate.enums.Workflow;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Scanner;
import java.util.logging.Logger;

import com.owino.mobiletranslate.TranslateApplication;
import com.owino.mobiletranslate.enums.TargetOS;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunnerInputReader {

    public static File getOutputFileFromPrompt() {
        log.info("Enter the root file with pre-translation text....");

        var scanner = new Scanner(System.in);
        var filePath = scanner.nextLine();
        var outputFile = new File(filePath);

        boolean fileExists = Files.exists(outputFile.toPath(), LinkOption.NOFOLLOW_LINKS);

        if (!fileExists) {
            log.info("This output file does not exist");
            outputFile = getOutputFileFromPrompt();
        } else {
            log.info("Processing translation into output file [" + filePath + "]");
        }

        return outputFile;
    }

    public static TargetOS getTargetOSFromUser() {
        log.info("What is the target mobile operating system for this localization? type in (A) for Android or (I) for iOS...");

        var scanner = new Scanner(System.in);

        var option = scanner.nextLine();

        switch (option) {
            case "A":
                return TargetOS.ANDROID;
            case "I":
                return TargetOS.IOS;
            default:
                throw new AssertionError("Invalid OS option " + option);
        }
    }

    public static File requestRootLocalizableFile() {
        log.info("Enter path to root localizable file...");

        var filePath = new Scanner(System.in).nextLine();
        var localizableFile = new File(filePath);

        if (Files.exists(localizableFile.toPath(), LinkOption.NOFOLLOW_LINKS)) {
            return localizableFile;
        } else {
            log.info("This localizable file does not exist");
            localizableFile = requestRootLocalizableFile();
        }
        return localizableFile;
    }

    public static String requestRootDestinationFolder() {
        log.info("Enter the folder path where you would like us to store the final translated files...");

        var folderPath = new Scanner(System.in).nextLine();

        var localizableFolder = new File(folderPath);

        if (Files.exists(localizableFolder.toPath(), LinkOption.NOFOLLOW_LINKS)) {
            return folderPath;
        } else {
            log.info("This folder does not exist");
            folderPath = requestRootDestinationFolder();
        }
        return folderPath;
    }

    public static File requestLocalizableFileForValidation() {
        log.info("Enter path to localizable file...");

        var filePath = new Scanner(System.in).nextLine();
        var localizableFile = new File(filePath);

        if (Files.exists(localizableFile.toPath(), LinkOption.NOFOLLOW_LINKS)) {
            return localizableFile;
        } else {
            log.info("This localizable file does not exist");
            localizableFile = requestLocalizableFileForValidation();
        }

        return localizableFile;
    }

    public static Workflow getWorkflowFromUser() {
        log.info("Which workflow would you like to run? type in \n(L) for Translation\n(V) for localized syntax analysis\n(F) For Freighting localized Strings...");
        var scanner = new Scanner(System.in);

        var option = scanner.nextLine();

        switch (option) {
            case "L":
                return Workflow.TRANSLATION;
            case "V":
                return Workflow.VALIDATION;
            case "F":
                return Workflow.FREIGHT;
            default:
                throw new AssertionError("Invalid option " + option);
        }
    }
}
