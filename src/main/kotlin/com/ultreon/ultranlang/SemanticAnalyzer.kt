package com.ultreon.ultranlang

import com.ultreon.ultranlang.annotations.Visit
import com.ultreon.ultranlang.ast.*
import com.ultreon.ultranlang.error.ErrorCode
import com.ultreon.ultranlang.error.SemanticException
import com.ultreon.ultranlang.symbol.FuncSymbol
import com.ultreon.ultranlang.symbol.VarSymbol
import com.ultreon.ultranlang.token.Token
import java.util.*

class SemanticAnalyzer : NodeVisitor() {
    var currentScope: ScopedSymbolTable? = null

    fun log(msg: Any?) {
        if (SHOULD_LOG_SCOPE) {
            println(Objects.toString(msg))
        }
    }

    fun error(errorCode: ErrorCode, token: Token) {
        throw SemanticException(errorCode, token, "${errorCode.value} -> $token")
    }

    @Visit(Block::class)
    fun visitBlock(node: Block) {
        for (declaration in node.declarations) {
            this.visit(declaration)
        }
        this.visit(node.compoundStatement)
    }

    @Visit(Program::class)
    fun visitProgram(node: Program) {
        log("ENTER scope: global")
        val globalScope = ScopedSymbolTable(
            "global",
            1,
            this.currentScope  // null
        )

        globalScope.initBuiltins()
        this.currentScope = globalScope

        // visit subtree
        for (statement in node.statements) {
            this.visit(statement)
        }

        log(globalScope)

        this.currentScope = this.currentScope?.enclosingScope
        log("LEAVE scope: global")
    }

    @Visit(Compound::class)
    fun visitCompound(node: Compound) {
        for (child in node.children) {
            this.visit(child)
        }
    }

    @Visit(NoOp::class)
    fun visitNoOp(node: NoOp) {
        // do nothing
    }

    @Visit(BinOp::class)
    fun visitBinOp(node: BinOp) {
        this.visit(node.left)
        this.visit(node.right)
    }

    @Visit(FuncDeclaration::class)
    fun visitProcedureDecl(node: FuncDeclaration) {
        val procName = node.procName
        val procSymbol = FuncSymbol(procName)

        this.currentScope!!.insert(procSymbol)

        log("ENTER scope: $procName")

        // Scope for parameters and local variables
        val procedureScope = ScopedSymbolTable(
            procName,
            this.currentScope!!.scopeLevel + 1,
                this.currentScope
        )

        this.currentScope = procedureScope

        // Insert parameters into the procedure scope
        for (param in node.formalParams) {
//            println(currentScope)
            val paramType = this.currentScope!!.lookup(param.typeNode.value as String)
            val paramName = param.varNode.value as String
            val varSymbol = VarSymbol(paramName, paramType)
            this.currentScope!!.insert(varSymbol)
            procSymbol.formalParams.add(varSymbol)
        }

        for (statement in node.statements) {
            this.visit(statement)
        }

        this.log(procedureScope)

        this.currentScope = this.currentScope?.enclosingScope
        this.log("LEAVE scope: $procName")

        // accessed by the interpreter when executing the procedure call
        procSymbol.statements = node.statements
    }

    @Visit(VarDecl::class)
    fun visitVarDecl(node: VarDecl) {
        println("node = ${node}")
        val typeName = node.typeNode.value as String
        val typeSymbol = this.currentScope!!.lookup(typeName)

        // We have all the information we need to create a variable symbol.
        // Create the symbol and insert it into the symbol table.
        val varName = node.varNode.value as String
        val varSymbol = VarSymbol(varName, typeSymbol)

        // Signal on error if the table already has a symbol
        // with the same name
        if (this.currentScope!!.lookup(varName, currentScopeOnly = true) != null) {
            this.error(ErrorCode.DUPLICATE_ID, node.varNode.token)
        }

        this.currentScope!!.insert(varSymbol)
    }

    @Visit(Assign::class)
    fun visitAssign(node: Assign) {
        // right-hand side
        this.visit(node.right)
        // left-hand side
        this.visit(node.left)
    }

    @Visit(Var::class)
    fun visitVar(node: Var) {
        val varName = node.value as String
        val varSymbol = this.currentScope!!.lookup(varName)

        if (varSymbol == null) {
            this.error(ErrorCode.ID_NOT_FOUND, node.token)
        }
    }

    @Visit(Num::class)
    fun visitNum(node: Num) {
        // do nothing
    }

    @Visit(UnaryOp::class)
    fun visitUnaryOp(node: UnaryOp) {
        // do nothing
    }

    @Visit(FuncCall::class)
    fun visitProcedureCall(node: FuncCall) {
        for (paramNode in node.actualParams) {
            this.visit(paramNode)
        }

        val procSymbol = this.currentScope!!.lookup(node.procName) as FuncSymbol
        // accessed by the interpreter when executing the procedure call
        node.procSymbol = procSymbol
    }
}