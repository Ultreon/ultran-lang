package com.ultreon.ultranlang.classes

import com.ultreon.ultranlang.symbol.VarSymbol

abstract class ScriptMethod(override val name: String, override val isStatic: Boolean) : ClassMember {
    abstract fun call(instance: ScriptObject?, params: Array<VarSymbol>);
}