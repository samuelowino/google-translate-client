package com.owino.mobiletranslate.freighter.concept;

public interface DestinationFilesProcessor {

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
