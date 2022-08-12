package com.ultreon.ultranlang.exception;

import com.ultreon.ultranlang.token.Token;
import com.ultreon.ultranlang.token.TokenPos;
import org.jetbrains.annotations.Nullable;

public class SemanticException extends UltranLangException {
    public SemanticException(@Nullable ErrorCode errorCode, @Nullable String message) {
        super(errorCode, message);
    }

    public SemanticException(@Nullable ErrorCode errorCode, @Nullable Token token) {
        this(errorCode, token, null);
    }

    public SemanticException(@Nullable ErrorCode errorCode, @Nullable Token token, @Nullable String message) {
        super(errorCode, token, message);
    }

    public SemanticException(@Nullable ErrorCode errorCode, @Nullable TokenPos pos, @Nullable String message) {
        super(errorCode, pos, message);
    }

    public SemanticException(@Nullable String message) {
        super(message);
    }

    public SemanticException(@Nullable Token token, @Nullable String message) {
        super(token, message);
    }

    public SemanticException(@Nullable TokenPos pos, @Nullable String message) {
        super(pos, message);
    }

    public SemanticException(@Nullable Token token, @Nullable TokenPos pos, @Nullable String message) {
        super(token, pos, message);
    }

    public SemanticException(@Nullable ErrorCode errorCode, @Nullable Token token, @Nullable TokenPos pos, @Nullable String message) {
        super(errorCode, token, pos, message);
    }
}
