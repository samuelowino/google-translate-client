package com.owino.mobiletranslate.validators;

import com.owino.mobiletranslate.validators.model.LocalizableFormatError;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface LocalizableFormErrorDetector {
    /**
     * Finds and logs syntax errors on localizable files
     *
     * @param stringsFile File localeLocalizable.strings
     * @return List of error logs
     */
    List<LocalizableFormatError> findErrorInFile(File stringsFile) throws IOException;

    /**
     * Determines the localizable file to parse through validation
     *
     * @return File
     */
    File requestFile();

    /**
     * Reports the errors found in file
     *
     * @param errors List<LocalizableFormatError>
     */
    void reportErrors(List<LocalizableFormatError> errors);
}
