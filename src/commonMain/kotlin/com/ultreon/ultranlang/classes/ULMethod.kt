package com.ultreon.ultranlang.classes

import com.ultreon.ultranlang.symbol.FuncSymbol

abstract class ULMethod(override val name: String, override val isStatic: Boolean) : ClassMember {
    open lateinit var symbol: FuncSymbol

    abstract fun call(instance: ULObject?, params: Array<ULObject>): Any?
    abstract val paramTypes: List<ULClass>
}