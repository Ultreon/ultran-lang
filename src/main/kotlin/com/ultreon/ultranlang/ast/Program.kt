package com.ultreon.ultranlang.ast

import kotlin.String

class Program(var name: String) : AST() {
    var statements = mutableListOf<AST>()
}