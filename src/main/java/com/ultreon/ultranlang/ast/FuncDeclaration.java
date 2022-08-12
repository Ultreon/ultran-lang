package com.ultreon.ultranlang.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FuncDeclaration extends AST {
    private String procName;
    private List<Param> formalParams;

    private final List<AST> statements = new ArrayList<>();

    public FuncDeclaration(String procName, List<Param> formalParams) {
        this.procName = procName;
        this.formalParams = formalParams;
    }

    public String getProcName() {
        return procName;
    }

    public void setProcName(String procName) {
        this.procName = procName;
    }

    public List<Param> getFormalParams() {
        return formalParams;
    }

    public void setFormalParams(List<Param> formalParams) {
        this.formalParams = formalParams;
    }

    public List<AST> getStatements() {
        return Collections.unmodifiableList(statements);
    }

    public void addStatement(AST statement) {
        statements.add(statement);
    }
}
