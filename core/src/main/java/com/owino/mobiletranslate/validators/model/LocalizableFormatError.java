package com.owino.mobiletranslate.validators.model;

import com.owino.mobiletranslate.enums.LocalizeErrorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LocalizableFormatError {
    private String lineStatement;
    private LocalizeErrorType errorTypes;
    private String errorDescription;
    private String suggestedFix;
}
