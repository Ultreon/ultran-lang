package com.ultreon.ultranlang.symbol;

public class BuiltinTypeSymbol extends Symbol {
    public static final String INTEGER = "INTEGER";
    public static final String REAL = "REAL";
    public static final String STRING = "STRING";

    public BuiltinTypeSymbol(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return getName();
    }
}
