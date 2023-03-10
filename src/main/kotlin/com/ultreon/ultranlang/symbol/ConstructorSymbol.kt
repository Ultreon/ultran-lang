package com.ultreon.ultranlang.symbol

class ConstructorSymbol(name: String, formalParams: List<VarSymbol>? = null, private val classSymbol: ClassSymbol) : FuncSymbol(name, formalParams, classSymbol.calls)