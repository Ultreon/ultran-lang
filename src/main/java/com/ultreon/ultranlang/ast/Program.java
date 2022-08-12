package com.ultreon.ultranlang.ast;

import java.util.ArrayList;
import java.util.List;

public class Program extends AST {
    private String name;
    private List<AST> statements = new ArrayList<>();

    public Program(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AST> getStatements() {
        return statements;
    }

    public void setStatements(List<AST> statements) {
        this.statements = statements;
    }
}
