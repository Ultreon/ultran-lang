package com.ultreon.ultranlang.symbol

import com.ultreon.ultranlang.ActivationRecord
import com.ultreon.ultranlang.ast.AST
import com.ultreon.ultranlang.classes.ClassRef
import com.ultreon.ultranlang.classes.ScriptClass
import com.ultreon.ultranlang.classes.ScriptClasses

class ClassSymbol(name: String, private val classes: ScriptClasses) : Symbol(name) {
    val isNative: Boolean
        get() {
            return classes.exists(name)
        }
    lateinit var statements: List<AST>

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
}