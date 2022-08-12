package com.ultreon.ultranlang.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Compound extends AST {
    private final List<AST> children = new ArrayList<>();

    public List<AST> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(AST node) {
        children.add(node);
    }
}
