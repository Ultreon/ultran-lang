package com.ultreon.ultranlang.token;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("SpellCheckingInspection")
public record TokenType(@NotNull String value) {
    private static final Map<String, TokenType> VALUE_TO_INSTANCE = new HashMap<>();
    private static final List<TokenType> INSTANCES = new ArrayList<>();

    // single-character token types
    public static final TokenType PLUS = new TokenType("+");
    public static final TokenType MINUS = new TokenType("-");
    public static final TokenType MUL = new TokenType("*");
    public static final TokenType FLOAT_DIV = new TokenType("/");
    public static final TokenType LPAREN = new TokenType("(");
    public static final TokenType RPAREN = new TokenType(")");
    public static final TokenType LCURL = new TokenType("{");
    public static final TokenType RCURL = new TokenType("}");
    public static final TokenType SEMI = new TokenType(";");
    public static final TokenType DOT = new TokenType(".");
    public static final TokenType COLON = new TokenType(":");
    public static final TokenType COMMA = new TokenType(",");

    // block of reserved words
    public static final TokenType PROGRAM = new TokenType("PROGRAM");
    public static final TokenType INTEGER = new TokenType("INTEGER");
    public static final TokenType REAL = new TokenType("REAL");
    public static final TokenType STRING = new TokenType("STRING");
    public static final TokenType BOOLEAN = new TokenType("BOOLEAN");
    public static final TokenType TRUE = new TokenType("TRUE");
    public static final TokenType FALSE = new TokenType("FALSE");
    public static final TokenType INTEGER_DIV = new TokenType("DIV");
    public static final TokenType VAR = new TokenType("VAR");
    public static final TokenType FUNCTION = new TokenType("FUNCTION");
    public static final TokenType BEGIN = new TokenType("BEGIN");
    public static final TokenType END = new TokenType("END");
    public static final TokenType RETURN = new TokenType("RETURN");

    // misc
    public static final TokenType ID = new TokenType("ID");
    public static final TokenType INTEGER_CONST = new TokenType("INTEGER_CONST");
    public static final TokenType REAL_CONST = new TokenType("REAL_CONST");
    public static final TokenType STRING_CONST = new TokenType("INTEGER_CONST");
    public static final TokenType ASSIGN = new TokenType("ASSIGN");
    public static final TokenType EOF = new TokenType("EOF");

    public TokenType(@NotNull String value) {
        this.value = value;
        VALUE_TO_INSTANCE.put(value, this);
        INSTANCES.add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenType tokenType = (TokenType) o;
        return value.equals(tokenType.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }

    @Nullable
    public static TokenType fromString(String value) {
        return VALUE_TO_INSTANCE.get(value);
    }

    public static List<TokenType> getTypes() {
        return Collections.unmodifiableList(INSTANCES);
    }
}
