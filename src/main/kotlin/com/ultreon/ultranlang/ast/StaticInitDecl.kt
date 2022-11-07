package com.ultreon.ultranlang.ast

class StaticInitDecl() : AST(), ClassMemberDecl {
    val statements = mutableListOf<AST>()
}