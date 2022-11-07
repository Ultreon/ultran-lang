package com.ultreon.ultranlang.ast

import kotlin.String

class ConstructorDeclaration(var procName: String, val formalParams: List<Param> /* a list of Param nodes */) : AST(), ClassMemberDecl {
    val statements = mutableListOf<AST>()
}