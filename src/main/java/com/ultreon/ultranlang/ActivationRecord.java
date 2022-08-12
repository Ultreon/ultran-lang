package com.ultreon.ultranlang;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivationRecord {
    private String name;
    private ARType type;
    private int nestingLevel;

    private Map<String, Object> members = new HashMap<>();

    public ActivationRecord(String name, ARType type, int nestingLevel) {
        this.name = name;
        this.type = type;
        this.nestingLevel = nestingLevel;
    }

    public void set(String key, @Nullable Object value) {
        members.put(key, value);
    }

    @Nullable
    public Object get(String key) {
        return members.get(key);
    }

    public String toString() {
        List<String> lines = new ArrayList<>();
        lines.add("" + nestingLevel + ": " + type.name() + " " + name);

        for (Map.Entry<String, Object> entry : members.entrySet()) {
            lines.add("    " + entry.getKey() + " = " + entry.getValue());
        }

        return String.join("\n", lines);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ARType getType() {
        return type;
    }

    public void setType(ARType type) {
        this.type = type;
    }

    public int getNestingLevel() {
        return nestingLevel;
    }

    public void setNestingLevel(int nestingLevel) {
        this.nestingLevel = nestingLevel;
    }

    public Map<String, Object> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Object> members) {
        this.members = members;
    }
}
