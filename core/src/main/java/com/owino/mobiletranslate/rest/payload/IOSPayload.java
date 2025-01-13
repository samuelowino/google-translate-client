package com.owino.mobiletranslate.rest.payload;

import java.util.List;
import java.util.Set;

/**
 * represents an ios translation request payload
 * <p>
 *     An example
 *     {
 *     "workflow": "L",
 *     "targetOS": "IOS",
 *     "distinctLanguages": ["af", "ar", "az","sw"],
 *     "iosContent": [
 *       {
 *         "key": "health",
 *         "content": "i live"
 *       },
 *       {
 *         "name": "fitness",
 *         "content": "we up"
 *       }
 *     ]
 *   }'
 * </p>
 * @param workflow represents a service on the application
 * @param targetOS target operating system
 * @param distinctLanguages locales to xmlContent to be translated to
 * @param iosContent payload to be processed
 */

public record IOSPayload(String workflow,
                         String targetOS,
                         List<String> distinctLanguages,
                         List<IOSMessage> iosContent) implements TranslatePayload {
    @Override
    public String targetOS() {
        return "IOS";
    }
}
