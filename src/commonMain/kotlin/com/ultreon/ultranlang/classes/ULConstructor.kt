package com.ultreon.ultranlang.classes

import com.ultreon.ultranlang.symbol.VarSymbol

class ULConstructor(name: String) : ULMethod(name, false) {
    override fun call(instance: ULObject?, params: Array<VarSymbol>): ULObject {
        return PrimitiveVoid
    }
}