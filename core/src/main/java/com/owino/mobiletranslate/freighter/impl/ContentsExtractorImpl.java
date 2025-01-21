package com.owino.mobiletranslate.freighter.impl;

import com.owino.mobiletranslate.freighter.concept.ContentsExtractor;
import com.owino.mobiletranslate.freighter.utils.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentsExtractorImpl implements ContentsExtractor {

    private static final List<String> languages = Arrays.asList(
        "pl","am", "ar", "be", "bg", "bn",
        "bs", "fr", "ga",
        "ca", "cs", "da", "es", "et", "fi", "gl",
        "it", "iw", "ja", "ka", "km", "ko", "ky",
        "my", "nb", "ne", "nl", "no", "pa", "ro",
        "ru", "si", "sk", "sl", "sq", "sr",
        "sv", "sw", "ta", "te", "th", "tr", "vi", "zh", "zu", "kn", "kk",
        "af", "hi", "hr", "hu", "hy", "in", "lo", "lv",
        "mk", "ml", "pt", "uk", "ur", "ms", "is", "el", "mr", "de"
    );

    /**
     * Obtain the files contents as text
     *
     * @param sourceResourceFile - File source file
     * @return - String Contents
     */
    @Override
    public Map<String, String> getFileContents(BufferedReader sourceResourceFile, String language) throws IOException {
        String contents = IOUtils.readFileContents(sourceResourceFile);
        HashMap<String, String> mapContents = new HashMap<>();
        mapContents.put(language, contents);
        return mapContents;
    }

    /**
     * Delete the xml namespace if exists since android studio res files
     * will surely have this available.
     *
     * @param resContents - String resContents
     * @return - String clean contents
     */
    @Override
    public String deleteXmlNamesSpace(String resContents) {
        return resContents.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
    }

    /**
     * Delete closing namespace tag
     *
     * @param resContents - String resContents
     * @return - String clean contents
     */
    @Override
    public String deleteClosingResourceTagFromContents(String resContents) {
        return resContents.replace("<resources>", "");
    }

    /**
     * Returns a list of sources file containing source content
     *
     * @return - List of Files
     */
    @Override
    public Map<String, BufferedReader> getSourcesFiles() {
        Map<String, BufferedReader> sourceFiles = new HashMap<>();
        for (String language : languages) {
            var fileInputStream = IOUtils.loadResourceFileUrl(language + ".xml");
            var bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            sourceFiles.put(language, bufferedReader);
        }
        return sourceFiles;
    }

    /**
     * Replace file contents with new labels
     *
     * @param fullText           - String origin full text
     * @param contentToReplace   - String content, label or tags to replace
     * @param replacementContent - String content, label or tags to serve as replacements
     * @return String - Updated full text content
     */
    @Override
    public String replaceDestinationFileContents(String fullText, String contentToReplace, String replacementContent) {
        return fullText.replace(contentToReplace, replacementContent);
    }
}
