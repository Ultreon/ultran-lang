package com.ultreon.ultranlang.symbol

import com.ultreon.ultranlang.ActivationRecord
import com.ultreon.ultranlang.ast.LangObj
import com.ultreon.ultranlang.func.NativeCalls

open class FuncSymbol(name: String, formalParams: List<VarSymbol>? = null, private val calls: NativeCalls) : Symbol(name) {
    val isNative: Boolean
        get() {
            return calls.exists(name)
        }
    lateinit var statements: List<LangObj>
    val formalParams: MutableList<VarSymbol> = formalParams?.toMutableList() ?: mutableListOf()

    override fun toString(): String {
        return "<${this::class.simpleName}($name=$name, type=$type)>"
    }

    fun representation(): String = toString()
    fun callNative(ar: ActivationRecord): Any? {
        return calls.nativeCall(this, formalParams, ar)
    }
}