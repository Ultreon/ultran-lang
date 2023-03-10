package com.ultreon.ultranlang.symbol

import com.ultreon.ultranlang.ActivationRecord
import com.ultreon.ultranlang.ast.ClassInitDecl
import com.ultreon.ultranlang.ast.ClassMemberDecl
import com.ultreon.ultranlang.ast.FieldDecl
import com.ultreon.ultranlang.ast.LangObj
import com.ultreon.ultranlang.classes.ClassRef
import com.ultreon.ultranlang.classes.ScriptClass
import com.ultreon.ultranlang.classes.ScriptClasses
import com.ultreon.ultranlang.func.NativeCalls

class ClassSymbol(name: String, private val classes: ScriptClasses, private val parentCalls: NativeCalls) : Symbol(name) {
    lateinit var classInit: ClassInitDecl
    lateinit var fields: MutableList<FieldDecl>
    lateinit var members: MutableList<ClassMemberDecl>

    val calls = NativeCalls(parent = parentCalls)

    val isNative: Boolean
        get() {
            return classes.exists(name)
        }
    lateinit var statements: List<LangObj>

    override fun toString(): String {
        return "<${this::class.simpleName}($name=$name, type=$type)>"
    }

    fun representation(): String = toString()
    fun getRef(ar: ActivationRecord): ClassRef? {
        return classes.getRef(name)
    }
    fun getNative(ar: ActivationRecord): ScriptClass? {
        return classes[name]
    }

    fun invokeInternal(methodSymbol: MethodSymbol, formalParams: MutableList<VarSymbol>, ar: ActivationRecord): Any? {
        TODO("Not yet implemented")
    }
}