package com.ultreon.ultranlang.ast;

public class VarDecl extends AST {
    private Var varNode;
    private Type typeNode;

    public VarDecl(Var varNode, Type typeNode) {
        this.varNode = varNode;
        this.typeNode = typeNode;
    }

    public Var getVarNode() {
        return varNode;
    }

    public void setVarNode(Var varNode) {
        this.varNode = varNode;
    }

    public Type getTypeNode() {
        return typeNode;
    }

    public void setTypeNode(Type typeNode) {
        this.typeNode = typeNode;
    }
}
