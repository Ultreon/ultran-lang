package com.ultreon.ultranlang

import com.ultreon.ultranlang.annotations.Visit
import com.ultreon.ultranlang.ast.*
import com.ultreon.ultranlang.token.TokenType
import java.math.BigDecimal
import java.math.BigInteger

class Interpreter(val tree: Program?) : NodeVisitor() {
    val callStack: CallStack = CallStack()
    
    fun log(msg: Any?) {
        if (shouldLogStack) {
            println(msg)
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
    fun visitUnaryOp(node: UnaryOp): Any? {
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
        ar[varName] = varValue

        return null
    }

    @Visit(Var::class)
    fun visitVar(node: Var): Any? {
        val varName = node.value as String

        val ar = callStack.peek()
        return ar[varName]
    }

    @Visit(NoOp::class)
    fun visitNoOp(node: NoOp) {
        // do nothing
    }

    @Visit(FuncDeclaration::class)
    fun visitProcedureDecl(node: FuncDeclaration) {
        // do nothing
    }

    @Visit(FuncCall::class)
    fun visitProcedureCall(node: FuncCall): Any? {
        val procName = node.procName
        val procSymbol = node.procSymbol

        val ar = ActivationRecord(procName, ARType.PROCEDURE, procSymbol!!.scopeLevel + 1)

        val formalParams = procSymbol.formalParams
        val actualParams = node.actualParams

        for ((paramSymbol, argumentNode) in formalParams.zip(actualParams)) {
            ar[paramSymbol.name] = this.visit(argumentNode)
        }

        this.callStack.push(ar)

        this.log("ENTER: PROCEDURE $procName")
        this.log(this.callStack.toString())

        // evaluate procedure body
        val value: Any?

        if (procSymbol.isNative) {
            var returned = procSymbol.callNative(ar)
            if (returned == Unit) {
                returned = null
            }
            value = returned
        } else {
            for (statement in procSymbol.statements) {
                this.visit(statement)
            }
            value = null
        }

        this.log("LEAVE: PROCEDURE $procName")
        this.log(this.callStack.toString())

        this.callStack.pop()
        return value
    }

    fun interpret(): Any {
        val tree = this.tree ?: return ""

        val visit = this.visit(tree)
        return visit as Unit
    }
}

private operator fun Any?.unaryPlus(): Any? {
    return when (this) {
        is Byte -> +this
        is Short -> +this
        is Int -> +this
        is Long -> +this
        is Float -> +this
        is Double -> +this
        is BigInteger -> this.abs()
        is BigDecimal -> this.abs()
        else -> throw IllegalArgumentException("Unknown unary operator +")
    }
}

private operator fun Any?.unaryMinus(): Any? {
    return when (this) {
        is Byte -> +this
        is Short -> +this
        is Int -> +this
        is Long -> +this
        is Float -> +this
        is Double -> +this
        is BigInteger -> this.negate()
        is BigDecimal -> this.negate()
        else -> throw IllegalArgumentException("Unknown unary operator +")
    }
}

private operator fun Any?.plus(visit: Any?): Any? {
    return if (this is Byte && visit is Byte) {
        this + visit
    } else if (this is Short && visit is Short) {
        this + visit
    } else if (this is Int && visit is Int) {
        this + visit
    } else if (this is Long && visit is Long) {
        this + visit
    } else if (this is BigInteger && visit is BigInteger) {
        this.add(visit)
    } else if (this is BigDecimal && visit is BigDecimal) {
        this.add(visit)
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
    } else if (this is BigInteger && visit is BigInteger) {
        this.subtract(visit)
    } else if (this is BigDecimal && visit is BigDecimal) {
        this.subtract(visit)
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

private operator fun Any?.times(visit: Any?): Any? {
    return if (this is Byte && visit is Byte) {
        this * visit
    } else if (this is Short && visit is Short) {
        this * visit
    } else if (this is Int && visit is Int) {
        this * visit
    } else if (this is Long && visit is Long) {
        this * visit
    } else if (this is BigInteger && visit is BigInteger) {
        this.multiply(visit)
    } else if (this is BigDecimal && visit is BigDecimal) {
        this.multiply(visit)
    } else {
        throw IllegalArgumentException("Cannot multiply $this and $visit")
    }
}

private operator fun Any?.div(visit: Any?): Any? {
    return if (this is Byte && visit is Byte) {
        this / visit
    } else if (this is Short && visit is Short) {
        this / visit
    } else if (this is Int && visit is Int) {
        this / visit
    } else if (this is Long && visit is Long) {
        this / visit
    } else if (this is BigInteger && visit is BigInteger) {
        this.divide(visit)
    } else if (this is BigDecimal && visit is BigDecimal) {
        this.divide(visit)
    } else {
        throw IllegalArgumentException("Cannot add $this and $visit")
    }
}
