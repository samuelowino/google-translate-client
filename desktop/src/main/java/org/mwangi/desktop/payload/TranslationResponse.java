package org.mwangi.desktop.payload;

import java.util.Map;

/**
 * This record represents our response payload
 *<p>
 *     Example:
 *     {"workflow":"TRANSLATE",
 *     "targetOS":"ANDROID",
 *     "translations":{
 *     "ar":
 *        {"fitness":"نحن نرتفع",
 *         "health":"انا اعيش"},
 *      "sw":
 *          {"fitness":"sisi juu",
 *          "health":"ninaishi"},
 *       "af":
 *          {"fitness":"ons op",
 *           "health":"ek leef"},
 *        "az":
 *          {"fitness":"qalxırıq",
 *           "health":"yaşayıram"
 *           }
 *         }}
 *</p>
 * @param workflow service you requested
 * @param targetOS operating system you targeted
 * @param translations request body sent back
 */
public record TranslationResponse(String workflow, String targetOS, Map<String,Map<String,String>> translations) {
}
