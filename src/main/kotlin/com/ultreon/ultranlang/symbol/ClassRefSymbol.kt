package com.ultreon.ultranlang.symbol

import com.ultreon.ultranlang.ActivationRecord
import com.ultreon.ultranlang.ast.LangObj
import com.ultreon.ultranlang.classes.ScriptObject
import com.ultreon.ultranlang.classes.ClassRef
import com.ultreon.ultranlang.classes.ScriptClasses

class ClassRefSymbol(name: String, formalParams: List<VarSymbol>? = null, private val classes: ScriptClasses) : Symbol(name) {
    val isNative: Boolean
        get() {
            return classes.exists(name)
        }
    lateinit var statements: List<LangObj>
    val formalParams: MutableList<VarSymbol> = formalParams?.toMutableList() ?: mutableListOf()

    override fun toString(): String {
        return "<${this::class.simpleName}($name=$name, type=$type)>"
    }

    fun representation(): String = toString()
    fun callNative(ar: ActivationRecord): ScriptObject {
        val scriptClass = classes[name] ?: throw IllegalArgumentException("Class Not Found: ")
        return scriptClass.newInstance(formalParams)
    }
    fun getRef(ar: ActivationRecord): ClassRef? {
        return classes.getRef(name)
    }
}