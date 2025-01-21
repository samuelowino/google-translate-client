package com.owino.mobiletranslate.validators.impl;

import com.owino.mobiletranslate.common.RunnerInputReader;
import com.owino.mobiletranslate.enums.LocalizeErrorType;
import com.owino.mobiletranslate.validators.LocalizableFormErrorDetector;
import com.owino.mobiletranslate.validators.model.LocalizableFormatError;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LocalizableFormErrorDetectorImpl implements LocalizableFormErrorDetector {
    @Override
    public List<LocalizableFormatError> findErrorInFile(File stringsFile) throws IOException {

        //log.info("findErrorInFile FILE " + stringsFile.toPath());

        List<LocalizableFormatError> errors = new ArrayList<>();

        var missingEqualSign = Files.lines(stringsFile.toPath(), StandardCharsets.UTF_16)
                .filter(line -> !line.contains("=") || line.contains("/"))
                .map(line -> new LocalizableFormatError(line, LocalizeErrorType.MISSING_EQUAL_SIGN,
                        LocalizeErrorType.MISSING_EQUAL_SIGN_DESC,"Add equal sign [=] between components"))
                .collect(Collectors.toList());

        var missingClosingSemiColon = Files.lines(stringsFile.toPath(), StandardCharsets.UTF_16)
                .filter(line -> !line.contains(";"))
                .map(line -> new LocalizableFormatError(line, LocalizeErrorType.MISSING_SEMI_COLON,
                        LocalizeErrorType.MISSING_SEMI_COLON_DESC,"Append semi colon sign [;] at end of line"))
                .collect(Collectors.toList());

        var missingOpeningQuotation =
                Files.lines(stringsFile.toPath(), StandardCharsets.UTF_16)
                        .filter(line -> !line.startsWith("\""))
                        .map(line -> new LocalizableFormatError(line, LocalizeErrorType.MISSING_OPENSING_QUOTES,
                                LocalizeErrorType.MISSING_OPENING_QUOTES_DESC,"Append semi colon sign [;] at end of line"))
                        .collect(Collectors.toList());

        errors.addAll(missingEqualSign);
        errors.addAll(missingClosingSemiColon);
        errors.addAll(missingOpeningQuotation);

        return errors;
    }

    @Override
    public File requestFile() {
        return RunnerInputReader.requestLocalizableFileForValidation();
    }

    @Override
    public void reportErrors(List<LocalizableFormatError> errors) {
        //log.debug("Found " + errors.size() + " errors in this file");
        for (LocalizableFormatError error : errors) {
            //log.info(error.getLineStatement() + "\n==>" + error.getErrorDescription());
        }
    }
}
