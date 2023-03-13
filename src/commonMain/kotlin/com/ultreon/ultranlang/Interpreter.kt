package com.ultreon.ultranlang

import com.soywiz.kbignum.BigInt
import com.soywiz.kbignum.BigNum
import com.ultreon.ultranlang.ast.*
import com.ultreon.ultranlang.classes.*
import com.ultreon.ultranlang.classes.internal.*
import com.ultreon.ultranlang.symbol.ConstructorSymbol
import com.ultreon.ultranlang.symbol.VarSymbol
import com.ultreon.ultranlang.token.TokenType
import kotlin.String

class Interpreter(val tree: Program?) : NodeVisitor() {
    private val objectStack: ObjectStack = ObjectStack()
    val callStack: CallStack = CallStack()
    
    fun log(msg: Any?) {
        if (shouldLogStack) {
            logger.debug(msg)
        }
    }

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

    fun visitBlock(node: Block) {
        for (declaration in node.declarations) {
            visit(declaration)
        }
        
        visit(node.compoundStatement)
    }

    fun visitVarDecl(node: VarDecl) {
        // do nothing
    }

    fun visitValDecl(node: ValDecl) {
        // do nothing
    }

    fun visitType(node: Type) {
        // do nothing
    }

    fun visitBinOp(node: BinOp): Any {
        return when (node.op.type) {
            TokenType.PLUS -> visit(node.left).plus(visit(node.right))
            TokenType.MINUS -> visit(node.left).minus(visit(node.right))
            TokenType.MUL -> visit(node.left).times(visit(node.right))
            TokenType.INTEGER_DIV -> visit(node.left).div(visit(node.right))
            else -> throw IllegalArgumentException("Unknown operator ${node.op.type}")
        }
    }

    fun visitNum(node: Num): Any? {
        return node.value
    }

    fun visitString(node: com.ultreon.ultranlang.ast.String): Any? {
        return node.value
    }

    fun visitUnaryOp(node: UnaryOp): Any {
        return when (node.op.type) {
            TokenType.PLUS -> visit(node.expr).unaryPlus()
            TokenType.MINUS -> visit(node.expr).unaryMinus()
            else -> throw IllegalArgumentException("Unknown operator ${node.op.type}")
        }
    }

    fun visitCompound(node: Compound): Any? {
        for (child in node.children) {
            visit(child)
        }

        return null
    }

    fun visitAssign(node: Assign): Any? {
        val varName = node.left.value
        val varValue = visit(node.right)

        val ar = callStack.peek()
        ar!![varName] = varValue

        return null
    }

    fun visitThis(node: ThisRef): Any? {
        var value = objectStack.peek()
        val child = node.child
        if (child != null) {
            if (child is VarRef) {
                value = visit(child) as ULObject?
                return value
            }
            if (child is MethodCall) {
                value = visit(child) as ULObject?
                return value
            }
        }

        return value
    }

    fun visitVarRef(node: VarRef): Any? {
        val varName = node.value

        val ar = callStack.peek()
        var value = ar!![varName]
        if (value is ULObject) {
            val child = node.child
            if (child != null) {
                if (child is VarRef) {
                    value = visit(child)
                    return value
                }
                if (child is MethodCall) {
                    value = visit(child)
                    return value
                }
            }
        }

        return value
    }

    fun visitNoOp(node: NoOp) {
        // do nothing
    }

    fun visitFuncDecl(node: FuncDeclaration) {
        // do nothing
    }

    fun visitMethodDecl(node: MethodDeclaration) {
        // do nothing
    }

    fun visitConstructorDecl(node: ConstructorDeclaration) {
        // do nothing
    }

    fun visitClassDecl(node: ClassDeclaration) {
        val className = node.className
        classes.register(object : ULClass(className) {
            override val staticFields: List<ULField> = node.staticFields.map { ULField(it.name, it.isStatic, it.isConstant) }
            override val staticMethods: List<ULMethod> = node.staticMethods.map { method ->
                val methodName = method.methodName
                object : ULMethod(methodName, method.isStatic) {
                    init {
                        symbol = method.methodSymbol
                    }

                    override val paramTypes: List<ULClass> = method.formalParams.map { param ->
                        classes[param.typeNode.value as String]!!
                    }

                    override fun call(instance: ULObject?, params: Array<ULObject>): Any? {
                        val ar = ActivationRecord("$className#$methodName", ARType.METHOD, method.methodSymbol.scopeLevel + 1)

                        this@Interpreter.callStack.push(ar)

                        for (statement in method.statements) {
                            visit(statement)
                        }

                        this@Interpreter.callStack.pop()

                        return null
                    }
                }
            }
            override val staticMembers: List<ClassMember> = mutableListOf<ClassMember>() + staticFields + staticMethods
            override val instanceFields: List<ULField> = node.instanceFields.map { ULField(it.name, it.isStatic, it.isConstant) }
            override val instanceMethods: List<ULMethod> = node.staticMethods.map { method ->
                val methodName = method.methodName
                object : ULMethod(methodName, method.isStatic) {
                    init {
                        symbol = method.methodSymbol
                    }

                    override val paramTypes: List<ULClass> = method.formalParams.map { param ->
                        classes[param.typeNode.value as String]!!
                    }

                    override fun call(instance: ULObject?, params: Array<ULObject>): Any? {
                        val ar = ActivationRecord("$className#$methodName", ARType.METHOD, method.methodSymbol.scopeLevel + 1)

                        this@Interpreter.callStack.push(ar)
                        this@Interpreter.objectStack.push(instance!!)

                        for (statement in method.statements) {
                            visit(statement)
                        }

                        this@Interpreter.callStack.pop()

                        return null
                    }
                }
            }
            override val instanceMembers: List<ClassMember>
                get() = instanceFields + instanceMethods
            override val constructors: List<ULConstructor> = node.constructors.map { constructor ->
                val methodName = "<init>"
                object : ULConstructor(methodName) {
                    init {
                        symbol = constructor.constructorSymbol
                    }

                    override val paramTypes: List<ULClass> by lazy {
                        constructor.formalParams.map { param ->
                            classes[param.typeNode.value as String] ?: throw Error("Class doesn't exist: ${param.typeNode.value}")
                        }
                    }

                    override fun call(instance: ULObject?, params: Array<ULObject>): PrimitiveVoid {
                        val ar = ActivationRecord("$className#$methodName", ARType.CONSTRUCTOR, constructor.constructorSymbol.scopeLevel + 1)

                        this@Interpreter.callStack.push(ar)
                        this@Interpreter.objectStack.push(instance!!)

                        for (statement in constructor.statements) {
                            visit(statement)
                        }

                        this@Interpreter.callStack.pop()

                        return PrimitiveVoid
                    }
                }
            }

            override fun invoke(arguments: List<ULObject>): ULObject {
                val ulObject = ULObject(this)
                var finalConstructor: ULConstructor? = null
                val formalTypes = arguments.map { it.`class` }
                for (constructor in constructors) {
                    if (constructor.paramTypes == formalTypes) {
                        finalConstructor = constructor
                    }
                }

                finalConstructor?.call(ulObject, arguments.toTypedArray())
                    ?: throw ExecutionException(classes["ultran/InvocationError"]!!.invoke(listOf(
                        PrimitiveString("Can't invoke class with the given arguments.")
                    )))

                return ulObject
            }

            override fun getConstructor(formalTypes: List<ULClass>): ULConstructor? {
                var finalConstructor: ULConstructor? = null
                for (constructor in constructors) {
                    if (constructor.paramTypes == formalTypes) {
                        finalConstructor = constructor
                    }
                }
                return finalConstructor
            }

            override fun getStaticMethod(name: String, formalTypes: List<ULClass>): ULMethod? {
                var finalMethod: ULMethod? = null
                for (method in staticMethods) {
                    if (method.paramTypes == formalTypes && method.name == name) {
                        finalMethod = method
                    }
                }
                return finalMethod
            }

            override fun getStaticField(name: String): ULField? {
                var finalField: ULField? = null
                for (field in staticFields) {
                    if (field.name == name) {
                        finalField = field
                    }
                }
                return finalField
            }

            override fun getInstanceMethod(name: String, formalTypes: List<ULClass>): ULMethod? {
                var finalMethod: ULMethod? = null
                for (method in instanceMethods) {
                    if (method.paramTypes == formalTypes && method.name == name) {
                        finalMethod = method
                    }
                }
                return finalMethod
            }

            override fun getInstanceField(name: String): ULField? {
                var finalField: ULField? = null
                for (field in instanceFields) {
                    if (field.name == name) {
                        finalField = field
                    }
                }
                return finalField
            }

            override fun init() {
                visit(node.classInit)
            }

        }.also {
            node.symbol.ulClass = it
            it.init()
        })
    }

    fun visitClassInitDecl(node: ClassInitDecl) {
        for (statement in node.statements) {
            this.visit(statement)
        }
    }

    fun visitFuncCall(node: FuncCall): Any? {
        val funcName = node.funcName
        var funcSymbol = node.funcSymbol
        val classSymbol = node.classSymbol
        val parent = node.parent
        val curObj = objectStack.peek()

        val convertArgs = fun(args: List<Any?>): List<ULObject> = args.map {
            when (it) {
                is Byte -> return@map PrimitiveByte(it)
                is Short -> return@map PrimitiveShort(it)
                is Int -> return@map PrimitiveInt(it)
                is Long -> return@map PrimitiveLong(it)
                is Float -> return@map PrimitiveFloat(it)
                is Double -> return@map PrimitiveDouble(it)
                is BigInt -> return@map PrimitiveBigInt(it)
                is BigNum -> return@map PrimitiveBigDec(it)
                is Char -> return@map PrimitiveChar(it)
                is String -> return@map PrimitiveString(it)
                is Unit -> return@map PrimitiveVoid
                else -> throw ExecutionException(classes["ultran/VMInternalError"]?.invoke(listOf(
                    PrimitiveString("Internal primitive can't be cast.")
                )) ?: run {
                    throw Error("Can't find class: ultran/VMInternalError")
                })
            }
        }

        val ar: ActivationRecord
        val formalParams: MutableList<VarSymbol>
        val actualParams: List<LangObj>
        val args: MutableList<Any?>
        val objArgs: List<ULObject>
        var value: Any?

        if (funcSymbol == null && parent != null) {
            if (classSymbol == null) throw Error("Detected a method, but the class reference is gone.")

            if (objectStack.isEmpty()) throw Error("Object stack is empty, but there's a parent object.")
            if (curObj == null) {
                throw ExecutionException(classes["ultran/NullValueError"]!!.invoke(listOf(
                    PrimitiveString("Value $parent is null. (And null means none)")
                )))
            }

            ar = ActivationRecord(funcName, ARType.METHOD, classSymbol.scopeLevel + 1)

            args = mutableListOf()
            for (actualParam in node.actualParams) {
                val visit = this.visit(actualParam)
                args += visit
            }

            objArgs = convertArgs(args)

            this.callStack.push(ar)

            val method = curObj.getMethod(funcName, objArgs.map { it.`class` }) ?: throw Error("Method symbol for '$funcName' in class '${classSymbol.name}' is gone.")
            value = method.call(curObj, objArgs.toTypedArray())

            this.callStack.pop()

            val child = node.child
            if (child != null) {
                if (child is VarRef) {
                    value = visit(child)
                }
                if (child is FuncCall) {
                    value = visit(child)
                }
            }

            return value
        } else if (funcSymbol == null && classSymbol != null) {
            ar = ActivationRecord(funcName, ARType.CONSTRUCTOR, classSymbol.scopeLevel + 1)

            args = mutableListOf()
            for (actualParam in node.actualParams) {
                val visit = this.visit(actualParam)
                args += visit
            }

            objArgs = convertArgs(args)

            funcSymbol = classSymbol.ulClass.getConstructor(objArgs.map { it.`class` })?.symbol
                ?: throw Error("Constructor symbol in class '${classSymbol.name}' is gone.")
        } else {
            if (funcSymbol == null) {
                throw Error("Function symbol for '$funcName' is gone. Not sure where it went...")
            }

            ar = ActivationRecord(funcName, ARType.FUNCTION, funcSymbol.scopeLevel + 1)
            formalParams = funcSymbol.formalParams
            actualParams = node.actualParams
            args = mutableListOf()
            objArgs = convertArgs(args)

            for ((paramSymbol, argumentNode) in formalParams.zip(actualParams)) {
                val visit = this.visit(argumentNode)
                ar[paramSymbol.name] = visit
                args += visit
            }

        }

        if (funcSymbol is ConstructorSymbol) {
            if (classSymbol == null) {
                throw Error("Function symbol seems to be a class member, but doesn't contain the class symbol")
            }

            value = if (funcSymbol.isNative) {
                classSymbol.ulClass.invoke(objArgs)
            } else {
                funcSymbol.classSymbol.ulClass.invoke(objArgs)
                for (statement in funcSymbol.statements) {
                    this.visit(statement)
                }
                null
            }

            val child = node.child
            if (child != null) {
                if (child is VarRef) {
                    value = visit(child)
                }
                if (child is FuncCall) {
                    value = visit(child)
                }
            }

            return value
        }

        val child = node.child
        if (child != null) {
            if (child is VarRef) {
                value = visit(child)
                return value
            }
            if (child is MethodCall) {
                value = visit(child)
                return value
            }
        }

        this.callStack.push(ar)

        this.log("ENTER: FUNCTION $funcName")
        this.log(this.callStack.toString())

        // evaluate procedure body
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
        this[MethodDeclaration::class] = this::visitMethodDecl
        this[ConstructorDeclaration::class] = this::visitConstructorDecl
        this[ClassDeclaration::class] = this::visitClassDecl
        this[ClassInitDecl::class] = this::visitClassInitDecl
        this[VarDecl::class] = this::visitVarDecl
        this[ValDecl::class] = this::visitValDecl
        this[Assign::class] = this::visitAssign
        this[VarRef::class] = this::visitVarRef
        this[ThisRef::class] = this::visitThis
        this[Num::class] = this::visitNum
        this[UnaryOp::class] = this::visitUnaryOp
        this[FuncCall::class] = this::visitFuncCall
    }
}

private fun Any?.unaryPlus(): Any {
    return when (this) {
        is Byte -> +this
        is Short -> +this
        is Int -> +this
        is Long -> +this
        is Float -> +this
        is Double -> +this
        is BigInt -> this.abs()
        is BigNum -> if (this < BigNum("0")) this.unaryMinus() else this
        else -> throw IllegalArgumentException("Unknown unary operator +")
    }
}

private fun Any?.unaryMinus(): Any {
    return when (this) {
        is Byte -> -this
        is Short -> -this
        is Int -> -this
        is Long -> -this
        is Float -> -this
        is Double -> -this
        is BigInt -> -this
        is BigNum -> BigNum("0") - this
        else -> throw IllegalArgumentException("Unknown unary operator +")
    }
}

private fun Any?.plus(visit: Any?): Any {
    return if (this is Byte && visit is Byte) {
        this + visit
    } else if (this is Short && visit is Short) {
        this + visit
    } else if (this is Int && visit is Int) {
        this + visit
    } else if (this is Long && visit is Long) {
        this + visit
    } else if (this is Float && visit is Float) {
        this + visit
    } else if (this is Double && visit is Double) {
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

private fun Any?.minus(visit: Any?): Any {
    return if (this is Byte && visit is Byte) {
        this - visit
    } else if (this is Short && visit is Short) {
        this - visit
    } else if (this is Int && visit is Int) {
        this - visit
    } else if (this is Long && visit is Long) {
        this - visit
    } else if (this is Float && visit is Float) {
        this - visit
    } else if (this is Double && visit is Double) {
        this - visit
    } else if (this is BigInt && visit is BigInt) {
        this - visit
    } else if (this is BigNum && visit is BigNum) {
        this - visit
    } else {
        throw IllegalArgumentException("Cannot subtract $this and $visit")
    }
}

private fun Any?.times(visit: Any?): Any {
    return if (this is Byte && visit is Byte) {
        this * visit
    } else if (this is Short && visit is Short) {
        this * visit
    } else if (this is Int && visit is Int) {
        this * visit
    } else if (this is Long && visit is Long) {
        this * visit
    } else if (this is Float && visit is Float) {
        this * visit
    } else if (this is Double && visit is Double) {
        this * visit
    } else if (this is BigInt && visit is BigInt) {
        this * visit
    } else if (this is BigNum && visit is BigNum) {
        this * visit
    } else {
        throw IllegalArgumentException("Cannot multiply $this and $visit")
    }
}

private fun Any?.div(visit: Any?): Any {
    return if (this is Byte && visit is Byte) {
        this / visit
    } else if (this is Short && visit is Short) {
        this / visit
    } else if (this is Int && visit is Int) {
        this / visit
    } else if (this is Long && visit is Long) {
        this / visit
    } else if (this is Float && visit is Float) {
        this / visit
    } else if (this is Double && visit is Double) {
        this / visit
    } else if (this is BigInt && visit is BigInt) {
        this / visit
    } else if (this is BigNum && visit is BigNum) {
        this / visit
    } else {
        throw IllegalArgumentException("Cannot divide $this and $visit")
    }
}

private fun Any?.rem(visit: Any?): Any {
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
    } else {
        throw IllegalArgumentException("Cannot use modules on $this and $visit")
    }
}
