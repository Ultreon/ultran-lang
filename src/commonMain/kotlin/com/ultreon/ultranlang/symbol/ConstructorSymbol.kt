package com.ultreon.ultranlang.symbol

import com.ultreon.ultranlang.ActivationRecord
import com.ultreon.ultranlang.classes.ULObject

open class ConstructorSymbol(name: String, formalParams: List<VarSymbol>? = null, override val classSymbol: ClassSymbol) : FuncSymbol(name, formalParams, classSymbol.staticCalls), ClassMemberSymbol {
    override fun callNative(ar: ActivationRecord): ULObject {
        return super.callNative(ar) as ULObject
    }
}