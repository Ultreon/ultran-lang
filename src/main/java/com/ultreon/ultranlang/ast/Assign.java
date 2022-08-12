package com.ultreon.ultranlang.ast;

import com.ultreon.ultranlang.token.Token;

public class Assign extends AST {
    private Var left;
    private Token op;
    private AST right;

    public Assign(Var left, Token op, AST right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public Var getLeft() {
        return left;
    }

    public void setLeft(Var left) {
        this.left = left;
    }

    public Token getOp() {
        return op;
    }

    public void setOp(Token op) {
        this.op = op;
    }

    public AST getRight() {
        return right;
    }

    public void setRight(AST right) {
        this.right = right;
    }
}
