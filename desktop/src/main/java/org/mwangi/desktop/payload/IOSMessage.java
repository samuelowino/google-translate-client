package com.owino.mobiletranslate.rest.payload;

/**
 * represent structure of ios strings
 * <p>
 *     An example:
 *     "general-add-notes-msg" = "Add Notes";
 * </p>
 * @param key
 * @param content
 */
public record IOSMessage(String key,String content) {

}
