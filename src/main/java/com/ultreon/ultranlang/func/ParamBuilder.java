package com.ultreon.ultranlang.func;

import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;

public class ParamBuilder {
    private final Map<String, String> paramMap = new HashMap<>();

    public static ParamBuilder create() {
        return new ParamBuilder();
    }

    @Contract(value = "_,_ -> this", pure = true)
    public ParamBuilder add(String name, String type) {
        paramMap.put(name, type);
        return this;
    }

    public Map<String, String> build() {
        return paramMap;
    }
}
