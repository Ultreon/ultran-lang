package com.ultreon.ultranlang.ast;

import com.ultreon.ultranlang.token.Token;
import org.jetbrains.annotations.Nullable;

public class Num extends AST {
    private Token token;

    @Nullable
    private Object value;

    public Num(Token token) {
        this.token = token;
        this.value = token.getValue();
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    public void setValue(@Nullable Object value) {
        this.value = value;
    }
}
