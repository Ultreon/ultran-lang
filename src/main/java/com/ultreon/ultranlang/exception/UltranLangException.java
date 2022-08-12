package com.ultreon.ultranlang.exception;

import com.ultreon.ultranlang.token.Token;
import com.ultreon.ultranlang.token.TokenPos;
import org.jetbrains.annotations.Nullable;

public class UltranLangException extends RuntimeException {
    @Nullable
    private final ErrorCode errorCode;
    @Nullable
    private final Token token;
    @Nullable
    private final TokenPos pos;
    @Nullable
    private final String message;

    public UltranLangException(@Nullable ErrorCode errorCode, @Nullable String message) {
        this(errorCode, (Token) null, message);
    }

    public UltranLangException(@Nullable ErrorCode errorCode, @Nullable Token token, @Nullable String message) {
        this(errorCode, token, null, message);
    }

    public UltranLangException(@Nullable ErrorCode errorCode, @Nullable TokenPos pos, @Nullable String message) {
        this(errorCode, null, pos, message);
    }

    public UltranLangException(@Nullable String message) {
        this((Token) null, message);
    }

    public UltranLangException(@Nullable Token token, @Nullable String message) {
        this(token, null, message);
    }

    public UltranLangException(@Nullable TokenPos pos, @Nullable String message) {
        this((Token) null, pos, message);
    }

    public UltranLangException(@Nullable Token token, @Nullable TokenPos pos, @Nullable String message) {
        this(null, token, pos, message);
    }

    public UltranLangException(@Nullable ErrorCode errorCode, @Nullable Token token, @Nullable TokenPos pos, @Nullable String message) {
        super(message);
        this.errorCode = errorCode;
        this.token = token;
        this.pos = pos == null && token != null ? token.getPos() : pos;
        this.message = message;
    }

    @Nullable
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Nullable
    public Token getToken() {
        return token;
    }

    @Nullable
    public TokenPos getPos() {
        return pos;
    }

    @Override
    @Nullable
    public String getMessage() {
        return message;
    }
}
