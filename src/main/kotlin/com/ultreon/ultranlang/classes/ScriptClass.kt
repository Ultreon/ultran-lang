package com.ultreon.ultranlang.classes

import com.ultreon.ultranlang.symbol.VarSymbol

abstract class ScriptClass(val name: String) {
    abstract fun getMembers(): List<ClassMember>
    abstract fun newInstance(formalParams: List<VarSymbol>): ScriptObject
    abstract fun init();
}
