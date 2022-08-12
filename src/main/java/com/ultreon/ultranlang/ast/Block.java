package com.ultreon.ultranlang.ast;

import java.util.List;

public class Block extends AST {
    private final List<AST> declarations;
    private Compound compoundStatement;

    public Block(List<AST> declarations, Compound compoundStatement) {
        this.declarations = declarations;
        this.compoundStatement = compoundStatement;
    }

    public List<AST> getDeclarations() {
        return declarations;
    }

    public Compound getCompoundStatement() {
        return compoundStatement;
    }

    public void setCompoundStatement(Compound compoundStatement) {
        this.compoundStatement = compoundStatement;
    }
}
