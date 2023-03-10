package com.ultreon.ultranlang.ast

import kotlin.String

class MethodDeclaration(var methodName: String, val static: Boolean, val formalParams: List<Param>) : LangObj(), ClassMemberDecl {
    val statements = mutableListOf<LangObj>()
}