package com.owino.mobiletranslate.common;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class LanguagesDirectory {

     private final List<String> languages = Arrays.asList(
             "ar","az","be","bg","bn","bs","fr","pl",
             "ha", "am",  "ga", "ca", "cs", "da", "es",
             "et", "fa", "fi", "gl", "it", "iw", "ja",
             "jv", "ka", "km", "ko", "ky", "my", "nb",
             "ne", "nl", "no", "pa", "ro", "ru", "si",
             "sk", "sl", "sq", "sr", "sv", "sw", "ta",
             "te", "th", "tr", "vi", "zh", "zu", "kn", "kk",
             "af", "de", "fil", "hi", "hr", "hu", "hy", "id",
             "in", "lo", "lv", "mk", "ml", "mn", "pt",
             "uk", "ur", "ms", "is", "el", "mr", "haw","he","ku","la","lt","lb","so"
     );


    public List<String> findUniqueListOfLocales() {

        //log.info("Cleaning up locales list...");

        return languages.stream()
                .distinct()
                .peek(System.out::println)
                .collect(Collectors.toList());
    }
}
