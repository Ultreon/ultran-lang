package com.ultreon.ultranlang

import com.ultreon.ultranlang.ast.*
import com.ultreon.ultranlang.classes.ULClasses
import com.ultreon.ultranlang.error.ErrorCode
import com.ultreon.ultranlang.error.SemanticException
import com.ultreon.ultranlang.func.NativeCalls
import com.ultreon.ultranlang.symbol.*
import com.ultreon.ultranlang.token.Token
import kotlin.String

class SemanticAnalyzer(
    private val calls: NativeCalls,
    private val classes: ULClasses
) : NodeVisitor() {
    var currentScope: ScopedSymbolTable? = null
    val prefix = " ".repeat(10)

    fun log(msg: Any?) {
        if (shouldLogScope) {
            logger.debug("$prefix | $msg")
        }
    }

    fun error(errorCode: ErrorCode, token: Token, message: String? = null) {
        val location = token.location
        if (message != null) {
            throw SemanticException(errorCode, token, "$message @ $location")
        }
        throw SemanticException(errorCode, token, "${errorCode.value} @ $location")
    }

    fun visitBlock(node: Block) {
        for (declaration in node.declarations) {
            this.visit(declaration)
        }
        this.visit(node.compoundStatement)
    }

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

    fun visitCompound(node: Compound) {
        for (child in node.children) {
            this.visit(child)
        }
    }

    fun visitNoOp(node: NoOp) {
        // do nothing
    }

    fun visitBinOp(node: BinOp) {
        this.visit(node.left)
        this.visit(node.right)
    }

    fun visitFuncDecl(node: FuncDeclaration) {
        val name = node.funcName
        val symbol = FuncSymbol(name, calls = calls)

        this.currentScope!!.insert(symbol)

        log("ENTER function: $name")

        // Scope for parameters and local variables
        val scope = ScopedSymbolTable(name, this.currentScope!!.scopeLevel + 1, this.currentScope, calls)

        this.currentScope = scope

        // Insert parameters into the procedure scope
        for (param in node.formalParams) {
            val paramType = this.currentScope!!.lookup(param.typeNode.value as String)
            val paramName = param.varRefNode.value
            val varSymbol = VarSymbol(paramName, paramType, false)
            this.currentScope!!.insert(varSymbol)
            symbol.formalParams.add(varSymbol)
        }

        for (statement in node.statements) {
            this.visit(statement)
        }

        this.log(scope)

        this.currentScope = this.currentScope?.enclosingScope
        this.log("LEAVE function: $name")

        // accessed by the interpreter when executing the procedure call
        symbol.statements = node.statements
    }

    fun visitMethodDecl(node: MethodDeclaration) {
        val name = node.methodName
        val symbol = MethodSymbol(name, classSymbol = node.classDeclaration.symbol)

        this.currentScope!!.insert(symbol)

        log("ENTER method: $name")

        // Scope for parameters and local variables
        val scope = ScopedSymbolTable(name, this.currentScope!!.scopeLevel + 1, this.currentScope, calls)

        this.currentScope = scope

        // Insert parameters into the procedure scope
        for (param in node.formalParams) {
            val paramType = this.currentScope!!.lookup(param.typeNode.value as String)
            val paramName = param.varRefNode.value
            val varSymbol = VarSymbol(paramName, paramType, false)
            this.currentScope!!.insert(varSymbol)
            symbol.formalParams.add(varSymbol)
        }

        for (statement in node.statements) {
            this.visit(statement)
        }

        this.log(scope)

        this.currentScope = this.currentScope?.enclosingScope
        this.log("LEAVE method: $name")

        // accessed by the interpreter when executing the procedure call
        symbol.statements = node.statements
    }

    fun visitConstructorDecl(node: ConstructorDeclaration) {
        val name = "<init>"
        val symbol = ConstructorSymbol(name, classSymbol = node.classDeclaration.symbol)

        node.constructorSymbol = symbol

        this.currentScope!!.insert(symbol)

        log("ENTER constructor: $name")

        // Scope for parameters and local variables
        val oldScope = currentScope

        this.currentScope = node.classDeclaration.instanceScope

        // Insert parameters into the procedure scope
        for (param in node.formalParams) {
            val paramType = this.currentScope!!.lookup(param.typeNode.value as String)
            val paramName = param.varRefNode.value
            val varSymbol = VarSymbol(paramName, paramType, false)
            this.currentScope!!.insert(varSymbol)
            symbol.formalParams.add(varSymbol)
        }

        for (statement in node.statements) {
            this.visit(statement)
        }

        this.log(currentScope)

        this.currentScope = oldScope
        this.log("LEAVE constructor: $name")

        // accessed by the interpreter when executing the procedure call
        symbol.statements = node.statements
    }

    fun visitClassDecl(node: ClassDeclaration) {
        val name = node.className
        val symbol = ClassSymbol(name, classes = classes, parentCalls = calls)

        node.symbol = symbol

        this.currentScope!!.insert(symbol)

        log("ENTER class: $name")

        val oldScope = currentScope

        // Scope for parameters and local variables
        val staticScope = ScopedSymbolTable(name, this.currentScope!!.scopeLevel + 1, this.currentScope, symbol.staticCalls)
        val instanceScope = ScopedSymbolTable(name, staticScope.scopeLevel + 1, staticScope, symbol.instanceCalls)

        node.staticScope = staticScope
        node.instanceScope = instanceScope

        // Insert parameters into the procedure scope
        for (member in node.staticMembers) {
            if (member is MethodDeclaration) {
                member.`this` = symbol
                this.currentScope = instanceScope
                this.visit(member)
            } else if (member is FieldDecl) {
                if (member is LangObj) {
                    member.`this` = symbol
                    this.currentScope = instanceScope
                    this.visit(member)
                } else {
                    throw Error("Class member is not a language object.")
                }
            }
        }

        this.visit(node.classInit)

        this.currentScope = instanceScope
        for (member in node.instanceFields) {
            if (member is LangObj) {
                member.`this` = symbol
                this.currentScope = instanceScope
                this.visit(member)
            } else {
                throw Error("Class member is not a language object.")
            }
        }

        // Insert parameters into the procedure scope
        for (member in node.instanceMethods) {
            member.`this` = symbol
            this.currentScope = instanceScope
            this.visit(member)
        }

        // Insert parameters into the procedure scope
        for (member in node.constructors) {
            member.`this` = symbol
            this.currentScope = instanceScope
            this.visit(member)
        }

        this.log(staticScope)

        this.currentScope = oldScope
        this.log("LEAVE class: $name")

        symbol.statements = node.classInit.statements

        symbol.classInitDecl = node.classInit
        symbol.staticFieldsDecl = node.staticFields
        symbol.staticMethodsDecl = node.staticMethods
        symbol.staticMembersDecl = node.staticMembers

        symbol.constructorsDecl = node.constructors
        symbol.instanceFieldsDecl = node.instanceFields
        symbol.instanceMethodsDecl = node.instanceMethods
        symbol.instanceMembersDecl = node.instanceMembers
    }

    fun visitClassInitDecl(node: ClassInitDecl) {
        log("ENTER class initializer.")

        for (statement in node.statements) {
            visit(statement)
        }

        log("LEAVE class initializer")
    }

    fun visitVarDecl(node: VarDecl) {
        val typeName = node.typeNode.value as String
        val typeSymbol = this.currentScope!!.lookup(typeName)

        // We have all the information we need to create a variable symbol.
        // Create the symbol and insert it into the symbol table.
        val varName = node.varRefNode.value
        val varSymbol = VarSymbol(varName, typeSymbol, false)

        // Signal on error if the table already has a symbol
        // with the same name
        if (this.currentScope!!.lookup(varName, currentScopeOnly = true) != null) {
            this.error(ErrorCode.DUPLICATE_ID, node.varRefNode.token)
        }

        this.currentScope!!.insert(varSymbol)
    }

    fun visitValDecl(node: ValDecl) {
        val typeName = node.typeNode.value as String
        val typeSymbol = this.currentScope!!.lookup(typeName)

        // We have all the information we need to create a variable symbol.
        // Create the symbol and insert it into the symbol table.
        val varName = node.varRefNode.value
        val varSymbol = VarSymbol(varName, typeSymbol, true)

        // Signal on error if the table already has a symbol
        // with the same name
        log("varName = $varName @ ${node.varRefNode.token.location}")
        if (this.currentScope!!.lookup(varName, currentScopeOnly = true) != null) {
            this.error(ErrorCode.DUPLICATE_ID, node.varRefNode.token)
        }

        this.currentScope!!.insert(varSymbol)
    }

    fun visitAssign(node: Assign) {
        // right-hand side
        this.visit(node.right)
        // left-hand side
        this.visit(node.left)
    }

    fun visitVar(node: VarRef) {
        val varName = node.value
        val varSymbol = this.currentScope!!.lookup(varName)

        if (varSymbol == null) {
            this.error(ErrorCode.ID_NOT_FOUND, node.token)
        }
    }

    fun visitThis(node: ThisRef) {
        val varName = node.value as String
//        val varSymbol = this.currentScope!!.lookup(varName)

        val classDeclaration = node.classDeclaration

//        if (varSymbol == null) {
//            this.error(ErrorCode.ID_NOT_FOUND, node.token)
//        }
    }

    fun visitNum(node: Num) {
        // do nothing
    }

    fun visitUnaryOp(node: UnaryOp) {
        // do nothing
    }

    tailrec fun visitFuncCall(node: FuncCall) {
        for (paramNode in node.actualParams) {
            this.visit(paramNode)
        }

        when (val funcName = this.currentScope!!.lookup(node.funcName)) {
            is MethodSymbol -> {
                println("FUNC_NAME ${node.funcName}; METHOD")
                // accessed by the interpreter when executing the procedure call
                node.funcSymbol = funcName
                node.classSymbol = funcName.classSymbol
            }
            is FuncSymbol -> {
                println("FUNC_NAME ${node.funcName}; FUNC")
                // accessed by the interpreter when executing the procedure call
                node.funcSymbol = funcName
            }
            is ClassSymbol -> {
                // accessed by the interpreter when executing the procedure call
                node.classSymbol = funcName
            }
            null -> {
                error("Function called '${node.funcName}' doesn't exist.")
            }

            else -> {
                error("'${node.funcName}' is not a function.")
            }
        }

        val child = node.child
        if (child != null && child is FuncCall) {
            return visitFuncCall(child)
        }
    }

    fun visitMethodCall(node: MethodCall) {
        for (paramNode in node.actualParams) {
            this.visit(paramNode)
        }

        when (val funcName = this.currentScope!!.lookup(node.funcName)) {
            is MethodSymbol -> {
                // accessed by the interpreter when executing the procedure call
                node.methodSymbol = funcName
            }
            null -> {
                error("Function called '${node.funcName}' doesn't exist.")
            }

            else -> {
                error("'${node.funcName}' is not a function.")
            }
        }
    }

    init {
        this[Block::class] = this::visitBlock
        this[Program::class] = this::visitProgram
        this[Compound::class] = this::visitCompound
        this[NoOp::class] = this::visitNoOp
        this[BinOp::class] = this::visitBinOp
        this[FuncDeclaration::class] = this::visitFuncDecl
        this[MethodDeclaration::class] = this::visitMethodDecl
        this[ConstructorDeclaration::class] = this::visitConstructorDecl
        this[ClassDeclaration::class] = this::visitClassDecl
        this[ClassInitDecl::class] = this::visitClassInitDecl
        this[VarDecl::class] = this::visitVarDecl
        this[ValDecl::class] = this::visitValDecl
        this[Assign::class] = this::visitAssign
        this[VarRef::class] = this::visitVar
        this[ThisRef::class] = this::visitThis
        this[Num::class] = this::visitNum
        this[UnaryOp::class] = this::visitUnaryOp
        this[FuncCall::class] = this::visitFuncCall
    }
}