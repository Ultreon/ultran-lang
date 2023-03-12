package com.ultreon.ultranlang

import com.ultreon.ultranlang.annotations.Visit
import com.ultreon.ultranlang.ast.*
import com.ultreon.ultranlang.classes.ULObject
import com.ultreon.ultranlang.token.TokenType
import com.soywiz.kbignum.BigNum
import com.soywiz.kbignum.BigInt
import kotlin.String

class Interpreter(val tree: Program?) : NodeVisitor() {
    val callStack: CallStack = CallStack()
    
    fun log(msg: Any?) {
        if (shouldLogStack) {
            logger.debug(msg)
        }
    }
    
    @Visit(Program::class)
    fun visitProgram(node: Program) {
        val programName = node.name
        log("ENTER: PROGRAM $programName")

        val ar = ActivationRecord(programName, ARType.PROGRAM, 1)
        callStack.push(ar)

        log(callStack.toString())

        for (statement in node.statements) {
            visit(statement)
        }

        log("LEAVE: PROGRAM $programName")
        log(callStack.toString())

        callStack.pop()
    }
    
    @Visit(Block::class)
    fun visitBlock(node: Block) {
        for (declaration in node.declarations) {
            visit(declaration)
        }
        
        visit(node.compoundStatement)
    }
    
    @Visit(VarDecl::class)
    fun visitVarDecl(node: VarDecl) {
        // do nothing
    }
    
    @Visit(Type::class)
    fun visitType(node: Type) {
        // do nothing
    }
    
    @Visit(BinOp::class)
    fun visitBinOp(node: BinOp): Any? {
        return when (node.op.type) {
            TokenType.PLUS -> visit(node.left) + visit(node.right)
            TokenType.MINUS -> visit(node.left) - visit(node.right)
            TokenType.MUL -> visit(node.left) * visit(node.right)
            TokenType.INTEGER_DIV -> visit(node.left) / visit(node.right)
            else -> throw IllegalArgumentException("Unknown operator ${node.op.type}")
        }
    }

    @Visit(Num::class)
    fun visitNum(node: Num): Any? {
        return node.value
    }

    @Visit(com.ultreon.ultranlang.ast.String::class)
    fun visitString(node: com.ultreon.ultranlang.ast.String): Any? {
        return node.value
    }

    @Visit(UnaryOp::class)
    fun visitUnaryOp(node: UnaryOp): Any {
        return when (node.op.type) {
            TokenType.PLUS -> +visit(node.expr)
            TokenType.MINUS -> -visit(node.expr)
            else -> throw IllegalArgumentException("Unknown operator ${node.op.type}")
        }
    }

    @Visit(Compound::class)
    fun visitCompound(node: Compound): Any? {
        for (child in node.children) {
            visit(child)
        }

        return null
    }

    @Visit(Assign::class)
    fun visitAssign(node: Assign): Any? {
        val varName = node.left.value as String
        val varValue = visit(node.right)

        val ar = callStack.peek()
        ar!![varName] = varValue

        return null
    }

    @Visit(VarRef::class)
    fun visitVarRef(node: VarRef): Any {
        val varName = node.value as String

        val ar = callStack.peek()
        val obj = ar!![varName]
        if (obj is ULObject) {
            obj.members
        }

        return obj!!
    }

    @Visit(NoOp::class)
    fun visitNoOp(node: NoOp) {
        // do nothing
    }

    @Visit(FuncDeclaration::class)
    fun visitFuncDecl(node: FuncDeclaration) {
        // do nothing
    }

    @Visit(ClassDeclaration::class)
    fun visitClassDecl(node: ClassDeclaration) {
        for (classMemberDecl in node.instanceMembers) {
            if (classMemberDecl is ClassInitDecl) {
                for (statement in classMemberDecl.statements) {
                    this.visit(statement)
                }
            }
        }
    }

    @Visit(FuncCall::class)
    fun visitFuncCall(node: FuncCall): Any? {
        val funcName = node.funcName
        val funcSymbol = node.funcSymbol

        val ar = ActivationRecord(funcName, ARType.PROCEDURE, funcSymbol!!.scopeLevel + 1)

        val formalParams = funcSymbol.formalParams
        val actualParams = node.actualParams

        for ((paramSymbol, argumentNode) in formalParams.zip(actualParams)) {
            ar[paramSymbol.name] = this.visit(argumentNode)
        }

        this.callStack.push(ar)

        this.log("ENTER: FUNCTION $funcName")
        this.log(this.callStack.toString())

        // evaluate procedure body
        val value: Any?

        if (funcSymbol.isNative) {
            var returned = funcSymbol.callNative(ar)
            if (returned == Unit) {
                returned = null
            }
            value = returned
        } else {
            for (statement in funcSymbol.statements) {
                this.visit(statement)
            }
            value = null
        }

        this.log("LEAVE: FUNCTION $funcName")
        this.log(this.callStack.toString())

        this.callStack.pop()
        return value
    }

    fun interpret(): Any {
        val tree = this.tree ?: return ""

        val visit = this.visit(tree)
        return visit as Unit
    }

    init {
        this[Block::class] = this::visitBlock
        this[Program::class] = this::visitProgram
        this[Compound::class] = this::visitCompound
        this[NoOp::class] = this::visitNoOp
        this[BinOp::class] = this::visitBinOp
        this[FuncDeclaration::class] = this::visitFuncDecl
//        this[MethodDeclaration::class] = this::visitMethodDecl
//        this[ConstructorDeclaration::class] = this::visitConstructorDecl
        this[ClassDeclaration::class] = this::visitClassDecl
//        this[ClassInitDecl::class] = this::visitClassInitDecl
        this[VarDecl::class] = this::visitVarDecl
//        this[ValDecl::class] = this::visitValDecl
        this[Assign::class] = this::visitAssign
        this[VarRef::class] = this::visitVarRef
//        this[ThisRef::class] = this::visitThis
        this[Num::class] = this::visitNum
        this[UnaryOp::class] = this::visitUnaryOp
        this[FuncCall::class] = this::visitFuncCall
    }
}

private operator fun Any?.unaryPlus(): Any {
    return when (this) {
        is Byte -> +this
        is Short -> +this
        is Int -> +this
        is Long -> +this
        is Float -> +this
        is Double -> +this
        is BigInt -> this.abs()
        is BigNum -> if (this < BigNum("0")) -this else this
        else -> throw IllegalArgumentException("Unknown unary operator +")
    }
}

private operator fun Any?.unaryMinus(): Any {
    return when (this) {
        is Byte -> -this
        is Short -> -this
        is Int -> -this
        is Long -> -this
        is Float -> -this
        is Double -> -this
        is BigInt -> -this
        is BigNum -> -this
        else -> throw IllegalArgumentException("Unknown unary operator +")
    }
}

private operator fun Any?.plus(visit: Any?): Any {
    return if (this is Byte && visit is Byte) {
        this + visit
    } else if (this is Short && visit is Short) {
        this + visit
    } else if (this is Int && visit is Int) {
        this + visit
    } else if (this is Long && visit is Long) {
        this + visit
    } else if (this is BigInt && visit is BigInt) {
        this + visit
    } else if (this is BigNum && visit is BigNum) {
        this + visit
    } else if (this is String && visit is String) {
        this + visit
    } else if (this is String && visit is Any) {
        this + visit
    } else if (this is Any && visit is String) {
        this.toString() + visit
    } else {
        throw IllegalArgumentException("Cannot add $this and $visit")
    }
}

private operator fun Any?.minus(visit: Any?): Any? {
    return if (this is Byte && visit is Byte) {
        this - visit
    } else if (this is Short && visit is Short) {
        this - visit
    } else if (this is Int && visit is Int) {
        this - visit
    } else if (this is Long && visit is Long) {
        this - visit
    } else if (this is BigInt && visit is BigInt) {
        this - visit
    } else if (this is BigNum && visit is BigNum) {
        this - visit
    } else if (this is String && visit is String) {
        this - visit
    } else if (this is String && visit is Any) {
        this - visit
    } else if (this is Any && visit is String) {
        this.toString() - visit
    } else {
        throw IllegalArgumentException("Cannot subtract $this and $visit")
    }
}

private operator fun Any?.times(visit: Any?): Any {
    return if (this is Byte && visit is Byte) {
        this * visit
    } else if (this is Short && visit is Short) {
        this * visit
    } else if (this is Int && visit is Int) {
        this * visit
    } else if (this is Long && visit is Long) {
        this * visit
    } else if (this is BigInt && visit is BigInt) {
        this * visit
    } else if (this is BigNum && visit is BigNum) {
        this * visit
    } else {
        throw IllegalArgumentException("Cannot multiply $this and $visit")
    }
}

private operator fun Any?.div(visit: Any?): Any {
    return if (this is Byte && visit is Byte) {
        this / visit
    } else if (this is Short && visit is Short) {
        this / visit
    } else if (this is Int && visit is Int) {
        this / visit
    } else if (this is Long && visit is Long) {
        this / visit
    } else if (this is BigInt && visit is BigInt) {
        this / visit
    } else if (this is BigNum && visit is BigNum) {
        this / visit
    } else {
        throw IllegalArgumentException("Cannot divide $this and $visit")
    }
}

private operator fun Any?.rem(visit: Any?): Any {
    return if (this is Byte && visit is Byte) {
        this % visit
    } else if (this is Short && visit is Short) {
        this % visit
    } else if (this is Int && visit is Int) {
        this % visit
    } else if (this is Long && visit is Long) {
        this % visit
    } else if (this is BigInt && visit is BigInt) {
        this % visit
    } else if (this is BigNum && visit is BigNum) {
        this % visit
    } else {
        throw IllegalArgumentException("Cannot use modules on $this and $visit")
    }
}