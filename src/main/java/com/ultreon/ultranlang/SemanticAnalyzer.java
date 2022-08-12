package com.ultreon.ultranlang;

import com.ultreon.ultranlang.annotations.Visit;
import com.ultreon.ultranlang.ast.*;
import com.ultreon.ultranlang.exception.ErrorCode;
import com.ultreon.ultranlang.exception.SemanticException;
import com.ultreon.ultranlang.symbol.FuncSymbol;
import com.ultreon.ultranlang.symbol.Symbol;
import com.ultreon.ultranlang.symbol.VarSymbol;
import com.ultreon.ultranlang.token.Token;
import org.jetbrains.annotations.Nullable;

public class SemanticAnalyzer extends NodeVisitor {
    private ScopedSymbolTable currentScope = null;

    public void log(@Nullable Object o) {
        if (Spi.SHOULD_LOG_SCOPE) {
            System.out.println(o);
        }
    }

    public void error(ErrorCode errorCode, @Nullable Token token) {
        throw new SemanticException(errorCode, token);
    }

    public ScopedSymbolTable getCurrentScope() {
        return currentScope;
    }

    public void setCurrentScope(ScopedSymbolTable currentScope) {
        this.currentScope = currentScope;
    }

    @Visit(Block.class)
    public void visitBlock(Block node) {
        for (AST declaration : node.getDeclarations()) {
            visit(declaration);
        }
        visit(node.getCompoundStatement());
    }

    @Visit(Program.class)
    public void visitProgram(Program node) {
        log("ENTER scope: global");
        ScopedSymbolTable globalScope = new ScopedSymbolTable("global", 1, currentScope);

        globalScope.initBuiltins();

        this.currentScope = globalScope;

        // visit subtree
        for (AST statement : node.getStatements()) {
            visit(statement);
        }

        log(globalScope);

        this.currentScope = this.currentScope.getEnclosingScope();
        log("LEAVE scope: global");
    }

    @Visit(Compound.class)
    public void visitCompound(Compound node) {
        for (AST child : node.getChildren()) {
            visit(child);
        }
    }

    @Visit(NoOp.class)
    public void visitNoOp(NoOp node) {
        // do nothing
    }

    @Visit(BinOp.class)
    public void visitBinOp(BinOp node) {
        visit(node.getLeft());
        visit(node.getRight());
    }

    @Visit(FuncDeclaration.class)
    public void visitFuncDeclaration(FuncDeclaration node) {
        String procName = node.getProcName();
        FuncSymbol funcSymbol = new FuncSymbol(procName);

        this.currentScope.insert(funcSymbol);

        log("ENTER scope: " + procName);

        ScopedSymbolTable funcScope = new ScopedSymbolTable(procName, currentScope.getScopeLevel() + 1, currentScope);

        this.currentScope = funcScope;

        // Insert parameters into the procedure's scope
        for (Param param : node.getFormalParams()) {
            Symbol paramType = this.currentScope.lookup((String) param.getTypeNode().getValue());
            String paramName = (String) param.getVarNode().getValue();
            VarSymbol varSymbol = new VarSymbol(paramName, paramType);
            this.currentScope.insert(varSymbol);
            funcSymbol.addFormalParam(varSymbol);
        }

        for (AST statement : node.getStatements()) {
            visit(statement);
        }

        this.log(funcScope);

        this.currentScope = this.currentScope.getEnclosingScope();
        this.log("LEAVE scope: " + procName);

        // accessed by the interpreter when executing the procedure call
        funcSymbol.setStatements(node.getStatements());
    }

    @Visit(VarDecl.class)
    public void visitVarDecl(VarDecl node) {
        String typeName = (String) node.getTypeNode().getValue();
        Symbol type = this.currentScope.lookup(typeName);

        // We have all the information we need to create a variable symbol.
        // Create the symbol and insert it into the symbol table.
        String varName = (String) node.getVarNode().getValue();
        VarSymbol varSymbol = new VarSymbol(varName, type);

        // Signal on error if the table already has a symbol
        // with the same name
        if (this.currentScope.lookup(varName, true) != null) {
            this.error(ErrorCode.DUPLICATE_ID, node.getVarNode().getToken());
        }

        this.currentScope.insert(varSymbol);
    }

    @Visit(Assign.class)
    public void visitAssign(Assign node) {
        // right-hand side
        visit(node.getRight());
        // left-hand side
        visit(node.getLeft());
    }

    @Visit(Var.class)
    public void visitVar(Var node) {
        String varName = (String) node.getValue();
        Symbol varSymbol = this.currentScope.lookup(varName);

        if (varSymbol == null) {
            this.error(ErrorCode.ID_NOT_FOUND, node.getToken());
        }
    }

    @Visit(Num.class)
    public void visitNum(Num node) {
        // do nothing
    }

    @Visit(Str.class)
    public void visitStr(Num node) {
        // do nothing
    }

    @Visit(UnaryOp.class)
    public void visitUnaryOp(UnaryOp node) {
        // do nothing
    }

    @Visit(FuncCall.class)
    public void visitFuncCall(FuncCall node) {
        for (AST paramNode : node.getActualParams()) {
            visit(paramNode);
        }

        FuncSymbol funcSymbol = (FuncSymbol) this.currentScope.lookup(node.getFuncName());
        // accessed by the interpreter when executing the procedure call
        node.setFuncSymbol(funcSymbol);
    }
}
