package com.ultreon.ultranlang.ast

import kotlin.String

class MethodDeclaration(var methodName: String, val formalParams: List<Param>) : AST(), ClassMemberDecl {
    val statements = mutableListOf<AST>()
}