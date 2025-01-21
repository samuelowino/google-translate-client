package com.owino.mobiletranslate.android.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import com.owino.mobiletranslate.android.model.Resources;
import com.owino.mobiletranslate.android.model.String;
import com.owino.mobiletranslate.android.translate.AndroidTranslateFactory;
import com.owino.mobiletranslate.rest.payload.TranslationResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XmlParserReader {



    public void executeXmlAndTranslationParser(File outputFile, List<java.lang.String> targetLanguages) {

        log.info("Translating " + targetLanguages.size() + " locales to target output file " + outputFile.getAbsolutePath());

        for (java.lang.String targetLanguage : targetLanguages) {

            Resources untranslatedResources = fromXml(outputFile);

            AndroidTranslateFactory translateFactory = new AndroidTranslateFactory(untranslatedResources);
            Resources translatedResources = translateFactory.getTranslatedResources(targetLanguage);

            for (String string : translatedResources.getStrings()) {
                log.info(string.toString());
            }

            toXml(translatedResources,targetLanguage + ".xml");
        }
    }
    /*
     *
     * stuck with same name but method signatures are a bit different
     */
    public TranslationResponse executeXmlAndTranslationParser(Resources input, List<java.lang.String> targetLanguages) {

        log.info("Translating " + targetLanguages.size() );
        Map<java.lang.String, Map<java.lang.String, java.lang.String>> allTranslations = new HashMap<>();

        for (java.lang.String targetLanguage : targetLanguages) {


            AndroidTranslateFactory translateFactory = new AndroidTranslateFactory(input);
            Resources translatedResources = translateFactory.getTranslatedResources(targetLanguage);
            Map<java.lang.String, java.lang.String> languageTranslations = new HashMap<>();
            for (String string : translatedResources.getStrings()) {
                log.info(string.toString());
               languageTranslations.put(string.getName(),string.getContent());
            }
            allTranslations.put(targetLanguage,languageTranslations);
        }
        return new TranslationResponse(
                "TRANSLATE",
                "ANDROID",
                allTranslations
        );

    }

    public void toXml(Resources contentRoot,java.lang.String outputFileName) {
        try {
            JAXBContext contextObj = JAXBContext.newInstance(Resources.class);
            Marshaller marshallerObj = contextObj.createMarshaller();
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshallerObj.marshal(contentRoot, new FileOutputStream(outputFileName));
        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public Resources fromXml(File outputFile) {
        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(Resources.class);

            Unmarshaller jaxbContextUnmarshaller = jaxbContext.createUnmarshaller();
            Resources resources = (Resources) jaxbContextUnmarshaller.unmarshal(outputFile);

            log.info("Resources\n" + resources);

            return resources;

        } catch (JAXBException e) {
            //testing stable with changes
            log.info("Invalid XML format detected in the pre-translate input. Please refer to the documentation for correct usage");
             e.printStackTrace();

            return null;
        }
    }

}
