package com.ultreon.ultranlang.symbol;

import org.jetbrains.annotations.Nullable;

public class Symbol {
    private final String name;

    @Nullable
    private final Symbol type;

    private int scopeLevel;

    public Symbol(String name) {
        this(name, null);
    }

    public Symbol(String name, @Nullable Symbol type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public Symbol getType() {
        return type;
    }

    public int getScopeLevel() {
        return scopeLevel;
    }

    public void setScopeLevel(int scopeLevel) {
        this.scopeLevel = scopeLevel;
    }

    @Override
    public String toString() {
        return "<%s(name=%s type=%s)>".formatted(getClass().getSimpleName(), name, type);
    }
}
