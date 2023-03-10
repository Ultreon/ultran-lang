package com.ultreon.ultranlang

import com.ultreon.ultranlang.annotations.Visit
import com.ultreon.ultranlang.ast.*
import com.ultreon.ultranlang.classes.ScriptClasses
import com.ultreon.ultranlang.error.ErrorCode
import com.ultreon.ultranlang.error.SemanticException
import com.ultreon.ultranlang.func.NativeCalls
import com.ultreon.ultranlang.symbol.ClassSymbol
import com.ultreon.ultranlang.symbol.FuncSymbol
import com.ultreon.ultranlang.symbol.VarSymbol
import com.ultreon.ultranlang.token.Token
import java.util.*

class SemanticAnalyzer(
    private val calls: NativeCalls,
    private val classes: ScriptClasses
) : NodeVisitor() {
    var currentScope: ScopedSymbolTable? = null

    fun log(msg: Any?) {
        if (shouldLogScope) {
            logger.debug(Objects.toString(msg))
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
        val globalScope = ScopedSymbolTable("global", 1, this.currentScope, calls)

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
    fun visitFuncDecl(node: FuncDeclaration) {
        val funcName = node.funcName
        val funcSymbol = FuncSymbol(funcName, calls = calls)

        this.currentScope!!.insert(funcSymbol)

        log("ENTER scope: $funcName")

        // Scope for parameters and local variables
        val functionScope = ScopedSymbolTable(funcName, this.currentScope!!.scopeLevel + 1, this.currentScope, calls)

        this.currentScope = functionScope

        // Insert parameters into the procedure scope
        for (param in node.formalParams) {
            val paramType = this.currentScope!!.lookup(param.typeNode.value as String)
            val paramName = param.varRefNode.value as String
            val varSymbol = VarSymbol(paramName, paramType)
            this.currentScope!!.insert(varSymbol)
            funcSymbol.formalParams.add(varSymbol)
        }

        for (statement in node.statements) {
            this.visit(statement)
        }

        this.log(functionScope)

        this.currentScope = this.currentScope?.enclosingScope
        this.log("LEAVE scope: $funcName")

        // accessed by the interpreter when executing the procedure call
        funcSymbol.statements = node.statements
    }

    @Visit(ClassDeclaration::class)
    fun visitClassDecl(node: ClassDeclaration) {
        val className = node.className
        val classSymbol = ClassSymbol(className, classes = classes, parentCalls = calls)

        this.currentScope!!.insert(classSymbol)

        log("ENTER class: $className")

        // Scope for parameters and local variables
        val functionScope = ScopedSymbolTable(className, this.currentScope!!.scopeLevel + 1, this.currentScope, classSymbol.calls)

        this.currentScope = functionScope

        // Insert parameters into the procedure scope
        for (member in node.members) {
            if (member is LangObj) {
                this.visit(member)
            } else {
                throw Error("Class member is not a language object.")
            }
        }

        this.log(functionScope)

        this.currentScope = this.currentScope?.enclosingScope
        this.log("LEAVE class: $className")

        // accessed by the interpreter when executing the procedure call
        classSymbol.statements = node.classInit.statements
        classSymbol.members = node.members
        classSymbol.fields = node.fields
        classSymbol.classInit = node.classInit
    }

    @Visit(VarDecl::class)
    fun visitVarDecl(node: VarDecl) {
        val typeName = node.typeNode.value as String
        val typeSymbol = this.currentScope!!.lookup(typeName)

        // We have all the information we need to create a variable symbol.
        // Create the symbol and insert it into the symbol table.
        val varName = node.varRefNode.value as String
        val varSymbol = VarSymbol(varName, typeSymbol)

        // Signal on error if the table already has a symbol
        // with the same name
        if (this.currentScope!!.lookup(varName, currentScopeOnly = true) != null) {
            this.error(ErrorCode.DUPLICATE_ID, node.varRefNode.token)
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

    @Visit(VarRef::class)
    fun visitVar(node: VarRef) {
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

        val funcName = this.currentScope!!.lookup(node.funcName) as FuncSymbol
        // accessed by the interpreter when executing the procedure call
        node.funcSymbol = funcName
    }
}