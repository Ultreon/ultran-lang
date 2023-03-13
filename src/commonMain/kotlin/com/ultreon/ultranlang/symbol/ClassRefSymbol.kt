package com.ultreon.ultranlang.symbol

import com.ultreon.ultranlang.ActivationRecord
import com.ultreon.ultranlang.ast.LangObj
import com.ultreon.ultranlang.classes.ClassHolder
import com.ultreon.ultranlang.classes.ULClasses
import com.ultreon.ultranlang.classes.ULObject

class ClassRefSymbol(name: String, formalParams: List<VarSymbol>? = null, private val classes: ULClasses) : Symbol(name) {
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
    fun callNative(ar: ActivationRecord): ULObject {
        val scriptClass = classes[name] ?: throw IllegalArgumentException("Class Not Found: ")
        return scriptClass.invoke(formalParams.map { ar[it.name] as ULObject })
    }
    fun getRef(ar: ActivationRecord): ClassHolder? {
        return classes.getRef(name)
    }
}