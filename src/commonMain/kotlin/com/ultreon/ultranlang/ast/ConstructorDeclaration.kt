package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.symbol.ClassSymbol
import com.ultreon.ultranlang.symbol.ConstructorSymbol

class ConstructorDeclaration(
    val formalParams: List<Param> /* a list of Param nodes */, override val classDeclaration: ClassDeclaration
) : LangObj(), ClassMemberDecl {
    lateinit var constructorSymbol: ConstructorSymbol
    lateinit var `this`: ClassSymbol
    val statements = mutableListOf<LangObj>()
}