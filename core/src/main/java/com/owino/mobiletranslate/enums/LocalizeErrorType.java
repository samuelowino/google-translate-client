package com.owino.mobiletranslate.enums;

public enum LocalizeErrorType {
    MISSING_SEMI_COLON,
    MISSING_CLOSING_QUOTE_CHARS,
    MISSING_EQUAL_SIGN,
    EMPTY_LINE,
    OTHER_ERROR,
    MISSING_OPENSING_QUOTES;

    public static final String MISSING_EQUAL_SIGN_DESC = "Missing Equal(=) Sign";
    public static final String MISSING_OPENING_QUOTES_DESC = "Missing Opening Quotation [\"]";
    public static final String MISSING_SEMI_COLON_DESC = "Missing Semi Colon [;]";
}
