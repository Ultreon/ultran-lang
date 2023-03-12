package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.symbol.ClassSymbol
import kotlin.String

class MethodDeclaration(var methodName: String, val isStatic: Boolean, val formalParams: List<Param>, override val classDeclaration: ClassDeclaration) : LangObj(), ClassMemberDecl {
    lateinit var `this`: ClassSymbol
    val statements = mutableListOf<LangObj>()
}