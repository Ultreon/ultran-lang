package com.ultreon.ultranlang.ast;

import com.ultreon.ultranlang.token.Token;

public class UnaryOp extends AST {
    private AST expr;
    private Token token;

    public UnaryOp(Token op, AST expr) {
        this.expr = expr;
        this.token = op;
    }

    public Token getOp() {
        return token;
    }

    public void setOp(Token op) {
        this.token = op;
    }

    public AST getExpr() {
        return expr;
    }

    public void setExpr(AST expr) {
        this.expr = expr;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
