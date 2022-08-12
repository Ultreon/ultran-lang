package com.ultreon.ultranlang.ast;

public abstract class AST {
    @Override
    public String toString() {
        return "<%s>".formatted(getClass().getSimpleName());
    }
}
