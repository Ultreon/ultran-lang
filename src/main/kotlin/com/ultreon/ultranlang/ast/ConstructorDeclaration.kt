package com.ultreon.ultranlang.ast

class ConstructorDeclaration(
    val formalParams: List<Param> /* a list of Param nodes */
) : LangObj(), ClassMemberDecl {
    val statements = mutableListOf<LangObj>()
}