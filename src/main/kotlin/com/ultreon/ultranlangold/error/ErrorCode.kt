package com.ultreon.ultranlangold.error

enum class ErrorCode(val value: String) {
    UNEXPECTED_TOKEN("Unexpected token"),
    ID_NOT_FOUND("Identifier not found"),
    DUPLICATE_ID("Duplicate id found"),
    DEBUG("Debug test"),
}