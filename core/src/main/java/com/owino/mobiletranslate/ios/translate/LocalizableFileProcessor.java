package com.owino.mobiletranslate.ios.translate;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.owino.mobiletranslate.ios.model.LocalizableTable;
import com.owino.mobiletranslate.rest.payload.IOSMessage;

public interface LocalizableFileProcessor {

    /**
     * Obtain the root Localizable file, often the english version of the localizable file.
     *
     * Subsequent translations will be done from the contents of this file.
     *
     * @return File , RootLocalizable file; can be project/en.lproj
     */
    File getRootLocalizableFile();

    /**
     * Extracts the localizable table components from a single line of localizable text
     *
     * "key" = "value-text" ==> Localizable(key,valueText)
     *
     * @param textLineOfLocalizable String
     * @return com.owino.mobiletranslate.ios.model.LocalizableTable
     *
     */

    LocalizableTable getLocalizableTableFromString(String textLineOfLocalizable);

    /**
     * Extracts the key-values from XCode Localizable.string file
     *
     * @param localizableFile File
     * @return List
     */
    List<LocalizableTable> extractLocalizableTableFromFile(File localizableFile);

    /**
     * Runs translation for each string in the localizable table and returns a translated set of localizable values
     *
     * @param originalLocalizableValues List
     * @param locale String such as en, fr ...
     * @return List of translated localizables
     */
    List<LocalizableTable> translateLocalizableTable(List<LocalizableTable> originalLocalizableValues, String locale);

    /**
     * Writes translations to a file {file.strings} and then places the file in specified directory
     *
     * @param translatedLocalizable List
     * @param locale String en, fr
     */
    void placeTranslatedTextInDestinationDir(List<LocalizableTable> translatedLocalizable,  String locale) throws IOException;

    /**
     * Create a destination file for the translated contents
     *
     * @param localeSymbol String such en, fr
     * @return File lo-localizable.string
     */
    File generateLocalizableDestinationFile(String localeSymbol) throws IOException;

    /**
     * Generates the root folder where the translated resources will be placed after translation
     *
     * @return File
     */
    File generateRootTranslationsOutputFolder() throws IOException;

    /**
     * Processes the request payload to a target language
     * @param languageTranslations request body payload
     * @param targetLanguage  locale to target
     * @return list of translated strings
     */
   List<IOSMessage> translateLocalizable(List<IOSMessage> languageTranslations,String targetLanguage);

}
