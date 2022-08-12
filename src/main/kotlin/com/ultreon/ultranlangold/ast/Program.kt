package com.ultreon.ultranlangold.ast

import kotlin.String

class Program(var name: String) : AST() {
    var statements = mutableListOf<AST>()
}