package com.ultreon.ultranlang.token;

import com.ultreon.ultranlang.Repr;

public class Token implements Repr {
    private TokenType type;
    private Object value;
    private int line;
    private int column;

    public Token(TokenType type, Object value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return "Token(%s, %s, %d, %d)".formatted(type, value, line, column);
    }

    public TokenPos getPos() {
        return new TokenPos(line, column);
    }
}
