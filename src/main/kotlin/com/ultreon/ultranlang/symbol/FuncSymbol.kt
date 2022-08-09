package com.ultreon.ultranlang.symbol

import com.ultreon.ultranlang.ActivationRecord
import com.ultreon.ultranlang.ast.Block
import com.ultreon.ultranlang.func.NativeCalls

class FuncSymbol(name: String, formalParams: List<VarSymbol>? = null) : Symbol(name) {
    val isNative: Boolean
        get() {
            return NativeCalls.exists(name)
        }
    lateinit var blockAst: Block
    val formalParams: MutableList<VarSymbol> = formalParams?.toMutableList() ?: mutableListOf()

    override fun toString(): String {
        return "<${this::class.qualifiedName}($name=$name, type=$type)>"
    }

    fun representation(): String = toString()
    fun callNative(ar: ActivationRecord) {
        NativeCalls.nativeCall(this, formalParams, ar)
    }
}