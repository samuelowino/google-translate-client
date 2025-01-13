package com.owino.mobiletranslate.ios;

import com.owino.mobiletranslate.common.TranslationProcessor;
import com.owino.mobiletranslate.ios.translate.LocalizableFileProcessor;
import com.owino.mobiletranslate.ios.translate.impl.LocalizableFileProcessorImpl;
import com.owino.mobiletranslate.rest.payload.IOSMessage;
import com.owino.mobiletranslate.rest.payload.TranslationResponse;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class iOSTranslationProcessor implements TranslationProcessor {
    private LocalizableFileProcessor processor;
    public iOSTranslationProcessor() {
        processor = new LocalizableFileProcessorImpl();
    }
    @Override
    public void runTranslation(File outputFile, List<String> targetLanguages) throws Exception {
        for (String targetLanguage : targetLanguages) {
            var localizableTable = processor.extractLocalizableTableFromFile(outputFile);
            var translatedTable = processor.translateLocalizableTable(localizableTable, targetLanguage);
            processor.placeTranslatedTextInDestinationDir(translatedTable, targetLanguage);
        }
    }
    public TranslationResponse runTranslation(List<IOSMessage> input, List<String> targetLanguages){
        Map<String, Map<String, String>> allTranslations = targetLanguages.stream()
                .collect(Collectors.toMap(
                        targetLanguage -> targetLanguage,
                        targetLanguage -> processor.translateLocalizable(input, targetLanguage).stream()
                                .collect(Collectors.toMap(IOSMessage::key, IOSMessage::content))
                ));

        return  new TranslationResponse(
                "TRANSLATION",
                "IOS",
                allTranslations
        );

    }
}
