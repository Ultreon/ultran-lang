package com.ultreon.ultranlang.exception;

public enum ErrorCode {
    UNEXPECTED_TOKEN("Unexpected token"),
    ID_NOT_FOUND("Identifier not found"),
    DUPLICATE_ID("Duplicate id found"),
    DEBUG("Debug test"),
    ;

    private final String value;

    ErrorCode(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
