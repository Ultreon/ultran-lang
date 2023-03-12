package com.ultreon.ultranlang.symbol

class MethodSymbol(name: String, formalParams: List<VarSymbol>? = null, private val clazz: ClassSymbol) : FuncSymbol(name, formalParams, clazz.staticCalls) {
    lateinit var classSymbol: ClassSymbol
}