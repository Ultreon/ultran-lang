package com.ultreon.ultranlang.symbol;

import com.ultreon.ultranlang.ActivationRecord;
import com.ultreon.ultranlang.ast.AST;
import com.ultreon.ultranlang.func.NativeCalls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FuncSymbol extends Symbol {
    @NotNull
    private final List<VarSymbol> formalParams;

    private List<AST> statements;

    public FuncSymbol(String name) {
        this(name, null);
    }

    public FuncSymbol(String name, @Nullable List<VarSymbol> formalParams) {
        super(name);
        this.formalParams = formalParams == null ? new ArrayList<>() : formalParams;
    }

    public boolean isNative() {
        return NativeCalls.exists(getName());
    }

    public List<VarSymbol> getFormalParams() {
        return Collections.unmodifiableList(formalParams);
    }

    public void setStatements(List<AST> statements) {
        this.statements = statements;
    }

    public List<AST> getStatements() {
        return statements;
    }

    @Nullable
    public Object callNative(ActivationRecord ar) {
        return NativeCalls.nativeCall(this, formalParams, ar);
    }

    public void addFormalParam(VarSymbol varSymbol) {
        formalParams.add(varSymbol);
    }
}
