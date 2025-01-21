package com.owino.mobiletranslate.android;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.owino.mobiletranslate.android.model.Resources;
import com.owino.mobiletranslate.android.xml.XmlParserReader;
import com.owino.mobiletranslate.common.TranslationProcessor;
import com.owino.mobiletranslate.rest.payload.TranslationResponse;

public class AndroidTranslationProcessor implements TranslationProcessor {
    public void runTranslation(File outputFile, List<String> targetLanguages){
        new XmlParserReader().executeXmlAndTranslationParser(outputFile, targetLanguages);
    }
    /*
      * stuck with same name but method signatures are a bit different
     */
    public TranslationResponse runTranslation(Resources input, List<String> targetLanguages){
     return    new XmlParserReader().executeXmlAndTranslationParser(input,targetLanguages);
    }
}
