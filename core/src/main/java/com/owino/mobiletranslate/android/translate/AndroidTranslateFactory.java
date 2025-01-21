package com.owino.mobiletranslate.android.translate;

import com.owino.mobiletranslate.android.model.Resources;
import com.owino.mobiletranslate.android.model.String;
import com.owino.mobiletranslate.googletranslate.GoogleTranslator;
import java.nio.charset.StandardCharsets;

public class AndroidTranslateFactory {

    private Resources resources;
    private GoogleTranslator translator;

    public AndroidTranslateFactory(Resources resources) {
        this.resources = resources;
        this.translator = new GoogleTranslator();
    }


    public Resources getTranslatedResources(java.lang.String targetLanguage) {
        return translateResources(targetLanguage);
    }

    private Resources translateResources(java.lang.String targetLanguage) {

        Resources translatedResources = new Resources();
        String[] translatedStringsArray = new String[resources.getStrings().length];

        com.owino.mobiletranslate.android.model.String[] strings = resources.getStrings();

        for (int i = 0; i < strings.length; i++) {
            String translatedStringEntry = new String();

            java.lang.String name = strings[i].getName();
            java.lang.String untranslatedContent = strings[i].getContent();
            java.lang.String translatedContent = new java.lang.String(translator.getTranslatedBytes(untranslatedContent, targetLanguage), StandardCharsets.UTF_16);

            //todo translate

            translatedStringEntry.setName(name);
            translatedStringEntry.setContent(translatedContent);

            translatedStringsArray[i] = translatedStringEntry;
        }

        translatedResources.setStrings(translatedStringsArray);

        return translatedResources;

    }

}


