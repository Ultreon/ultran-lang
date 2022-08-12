package com.ultreon.ultranlang.ast;

import com.ultreon.ultranlang.symbol.FuncSymbol;
import com.ultreon.ultranlang.token.Token;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FuncCall extends AST {
    private String funcName;
    private List<AST> actualParams;
    private Token token;

    @Nullable
    private FuncSymbol funcSymbol = null;

    public FuncCall(String funcName, List<AST> actualParams, Token token) {
        this.funcName = funcName;
        this.actualParams = actualParams;
        this.token = token;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public List<AST> getActualParams() {
        return actualParams;
    }

    public void setActualParams(List<AST> actualParams) {
        this.actualParams = actualParams;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    @Nullable
    public FuncSymbol getFuncSymbol() {
        return funcSymbol;
    }

    public void setFuncSymbol(@Nullable FuncSymbol funcSymbol) {
        this.funcSymbol = funcSymbol;
    }
}
