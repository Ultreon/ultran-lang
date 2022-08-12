package com.ultreon.ultranlang.exception;

import com.ultreon.ultranlang.token.Token;
import com.ultreon.ultranlang.token.TokenPos;
import org.jetbrains.annotations.Nullable;

public class ParserException extends UltranLangException {
    public ParserException(@Nullable ErrorCode errorCode, @Nullable String message) {
        super(errorCode, message);
    }

    public ParserException(@Nullable ErrorCode errorCode, @Nullable Token token, @Nullable String message) {
        super(errorCode, token, message);
    }

    public ParserException(@Nullable ErrorCode errorCode, @Nullable TokenPos pos, @Nullable String message) {
        super(errorCode, pos, message);
    }

    public ParserException(@Nullable String message) {
        super(message);
    }

    public ParserException(@Nullable Token token, @Nullable String message) {
        super(token, message);
    }

    public ParserException(@Nullable TokenPos pos, @Nullable String message) {
        super(pos, message);
    }

    public ParserException(@Nullable Token token, @Nullable TokenPos pos, @Nullable String message) {
        super(token, pos, message);
    }

    public ParserException(@Nullable ErrorCode errorCode, @Nullable Token token, @Nullable TokenPos pos, @Nullable String message) {
        super(errorCode, token, pos, message);
    }
}
