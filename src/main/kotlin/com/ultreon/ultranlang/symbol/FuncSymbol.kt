package com.ultreon.ultranlang.symbol

import com.ultreon.ultranlang.ActivationRecord
import com.ultreon.ultranlang.ast.AST
import com.ultreon.ultranlang.func.NativeCalls

class FuncSymbol(name: String, formalParams: List<VarSymbol>? = null) : Symbol(name) {
    val isNative: Boolean
        get() {
            return NativeCalls.exists(name)
        }
    lateinit var statements: List<AST>
    val formalParams: MutableList<VarSymbol> = formalParams?.toMutableList() ?: mutableListOf()

    override fun toString(): String {
        return "<${this::class.simpleName}($name=$name, type=$type)>"
    }

    fun representation(): String = toString()
    fun callNative(ar: ActivationRecord): Any? {
        return NativeCalls.nativeCall(this, formalParams, ar)
    }
}