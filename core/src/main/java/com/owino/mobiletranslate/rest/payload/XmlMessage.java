package com.owino.mobiletranslate.rest.payload;

/**
 * represents structure of html messages
 * <p>
 *     Example : <strings name="hello">world</strings>
 * </p>
 * @param name
 * @param content
 */
public record XmlMessage(String name,String content) {
}
