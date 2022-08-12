package com.ultreon.ultranlang.exception;

import com.ultreon.ultranlang.token.Token;
import com.ultreon.ultranlang.token.TokenPos;
import org.jetbrains.annotations.Nullable;

public class LexerException extends UltranLangException {
    public LexerException(@Nullable ErrorCode errorCode, @Nullable String message) {
        super(errorCode, message);
    }

    public LexerException(@Nullable ErrorCode errorCode, @Nullable Token token, @Nullable String message) {
        super(errorCode, token, message);
    }

    public LexerException(@Nullable ErrorCode errorCode, @Nullable TokenPos pos, @Nullable String message) {
        super(errorCode, pos, message);
    }

    public LexerException(@Nullable String message) {
        super(message);
    }

    public LexerException(@Nullable Token token, @Nullable String message) {
        super(token, message);
    }

    public LexerException(@Nullable TokenPos pos, @Nullable String message) {
        super(pos, message);
    }

    public LexerException(@Nullable Token token, @Nullable TokenPos pos, @Nullable String message) {
        super(token, pos, message);
    }

    public LexerException(@Nullable ErrorCode errorCode, @Nullable Token token, @Nullable TokenPos pos, @Nullable String message) {
        super(errorCode, token, pos, message);
    }
}
