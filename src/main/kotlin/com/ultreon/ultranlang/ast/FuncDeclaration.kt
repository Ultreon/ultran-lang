package com.ultreon.ultranlang.ast

import kotlin.String

open class FuncDeclaration(
    var procName: String, val formalParams: List<Param> /* a list of Param nodes */, var blockNode: Block
) : AST()