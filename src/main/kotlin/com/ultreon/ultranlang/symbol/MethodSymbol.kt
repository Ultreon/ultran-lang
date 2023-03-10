package com.ultreon.ultranlang.symbol

import com.ultreon.ultranlang.ActivationRecord
import com.ultreon.ultranlang.ast.LangObj

class MethodSymbol(name: String, formalParams: List<VarSymbol>? = null, private val clazz: ClassSymbol) : FuncSymbol(name, formalParams, clazz.calls) {
    lateinit var classSymbol: ClassSymbol
}