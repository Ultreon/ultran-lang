package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.symbol.ClassSymbol

class ConstructorDeclaration(
    val formalParams: List<Param> /* a list of Param nodes */, override val classDeclaration: ClassDeclaration
) : LangObj(), ClassMemberDecl {
    lateinit var `this`: ClassSymbol
    val statements = mutableListOf<LangObj>()
}