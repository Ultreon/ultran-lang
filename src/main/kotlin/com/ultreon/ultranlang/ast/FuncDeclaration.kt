package com.ultreon.ultranlang.ast

import kotlin.String

class FuncDeclaration(var procName: String, val formalParams: List<Param> /* a list of Param nodes */) : AST() {
    val statements = mutableListOf<AST>()
}