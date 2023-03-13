package com.ultreon.ultranlang.symbol

class MethodSymbol(name: String, formalParams: List<VarSymbol>? = null, override val classSymbol: ClassSymbol) : FuncSymbol(name, formalParams, classSymbol.staticCalls), ClassMemberSymbol {

}