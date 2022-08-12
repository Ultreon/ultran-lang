package com.ultreon.ultranlang.token;

import java.util.Objects;

public record TokenPos(int line, int column) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenPos tokenPos = (TokenPos) o;
        return line == tokenPos.line && column == tokenPos.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, column);
    }

    @Override
    public String toString() {
        return "<TokenPos(%d, %d)>".formatted(line, column);
    }
}
