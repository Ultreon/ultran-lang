package com.ultreon.ultranlang;

public interface Repr {
    default String representation() {
        return toString();
    }
}
