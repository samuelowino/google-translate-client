package com.owino.mobiletranslate.freighter.concept;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Extract and process
 * the contents of resources files for copying to android studio project
 */
public interface ContentsExtractor {

    /**
     * Obtain the files contents as text
     *
     * @param sourceResourceFile - File source file
     * @return - Map<String,String></> key - file locale, value - contents of the file
     * @throws IOException - Apache#IOUtils.reader might throw an IO Exception
     */
    Map<String, String> getFileContents(BufferedReader sourceResourceFile, String language) throws IOException;

    /**
     * Delete the xml namespace if exists since android studio res files
     * will surely have this available.
     *
     * @param resContents - String resContents
     * @return - String clean contents
     */
    String deleteXmlNamesSpace(String resContents);

    /**
     * Delete closing namespace tag
     *
     * @param resContents - String resContents
     * @return - String clean contents
     */
    String deleteClosingResourceTagFromContents(String resContents);

    /**
     * Returns a list of sources file containing source content
     *
     * @return - List of Files
     */
    Map<String, BufferedReader> getSourcesFiles();

    /**
     * Replace file contents with new labels
     *
     * @param fullText - String origin full text
     * @param contentToReplace - String content, label or tags to replace
     * @param replacementContent - String content, label or tags to serve as replacements
     * @return String - Updated full text content
     */
    String replaceDestinationFileContents(String fullText, String contentToReplace, String replacementContent);
}
