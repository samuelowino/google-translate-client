package com.owino.mobiletranslate.freighter;

import com.owino.mobiletranslate.freighter.concept.ContentMover;
import com.owino.mobiletranslate.freighter.concept.ContentsExtractor;
import com.owino.mobiletranslate.freighter.impl.ContentMoverImpl;
import com.owino.mobiletranslate.freighter.impl.ContentsExtractorImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
public class FreighterLauncher {

    public static void takeOffFreighter() throws IOException {
        File androidResDir = null;
        log.info("Enter destination folder for the translated files...\n");
        Scanner scanner = new Scanner(System.in);
        String androidStudioResDirPath = scanner.nextLine();
        if (androidStudioResDirPath.isEmpty()) {
            log.error("Invalid file path entered, exiting...");
            System.exit(0);
        } else {
            androidResDir = new File(androidStudioResDirPath);
        }

        ContentsExtractor contentsExtractor = new ContentsExtractorImpl();
        Map<String, BufferedReader> sourceFiles = contentsExtractor.getSourcesFiles();
//        List<>sourceFiles.stream().map(contentsExtractor::getFileContents).collect(Collectors.toList());
        List<Map<String, String>> mapList = new ArrayList<>();
        for (Map.Entry<String, BufferedReader> localeFileEntry : sourceFiles.entrySet()) {
            Map<String, String> contentsMap = contentsExtractor.getFileContents(localeFileEntry.getValue(), localeFileEntry.getKey());
            mapList.add(contentsMap);
        }

        List<Map<String, String>> cleanContentMapList = new ArrayList<>();

        for (Map<String, String> contentsMap : mapList) {
            for (Map.Entry<String, String> stringStringEntry : contentsMap.entrySet()) {
                var cleanedUpContent = contentsExtractor.deleteXmlNamesSpace(stringStringEntry.getValue());
                var evenCleanerContent = contentsExtractor.deleteClosingResourceTagFromContents(cleanedUpContent);
                var muchCleanerContent = contentsExtractor.replaceDestinationFileContents(evenCleanerContent, "strings", "string");

                Map<String, String> cleanContentMap = new HashMap<>();
                cleanContentMap.put(stringStringEntry.getKey(), muchCleanerContent);
                cleanContentMapList.add(cleanContentMap);
            }
        }

        ContentMover mover = new ContentMoverImpl();
        for (Map<String, String> contentsMap : cleanContentMapList) {
            for (Map.Entry<String, String> stringStringEntry : contentsMap.entrySet()) {
                mover.pasteContentsToAndroidStudioSourceDir(androidResDir, stringStringEntry.getKey(), stringStringEntry.getValue());
                List<String> additionalKeys = mover.checkIfMoveMultipleFoldersBySignatureIfNecessary(stringStringEntry.getKey());
                for (String additionalKey : additionalKeys) {
                    mover.pasteContentsToAndroidStudioSourceDir(androidResDir, additionalKey, stringStringEntry.getValue());
                }
            }
        }
    }
}
