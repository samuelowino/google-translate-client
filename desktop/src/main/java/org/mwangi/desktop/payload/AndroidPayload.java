package org.mwangi.desktop.payload;

import java.util.List;

/**
 * This  represents android request payload
 * <p>
 *     Example:
 *     "workflow": "L",
 *     "targetOS": "ANDROID",
 *     "distinctLanguages": ["af", "ar", "az","sw"],
 *     "xmlContent": [
 *       {
 *         "name": "health",
 *         "content": "i live"
 *       },
 *       {
 *         "name": "fitness",
 *         "content": "we up"
 *       }
 *     ]
 *   }
 *
 * </p>
 * @param workflow represents a service on the application
 * @param targetOS target operating system
 * @param distinctLanguages locales to xmlContent to be translated to
 * @param xmlContent payload to be processed
 */
public record AndroidPayload(String workflow,
                             String targetOS,
                             List<String> distinctLanguages,
                             List<XmlMessage> xmlContent)  implements TranslatePayload {
    @Override
    public String targetOS() {
        return "ANDROID";
    }
}
