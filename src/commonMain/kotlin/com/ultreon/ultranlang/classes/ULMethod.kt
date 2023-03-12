package com.ultreon.ultranlang.classes

import com.ultreon.ultranlang.symbol.VarSymbol

abstract class ULMethod(override val name: String, override val isStatic: Boolean) : ClassMember {
    abstract fun call(instance: ULObject?, params: Array<VarSymbol>): Any?
}