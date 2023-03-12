package com.ultreon.ultranlang.ast

class ClassInitDecl() : LangObj(), ClassMemberDecl {
    val statements = mutableListOf<LangObj>()
    override val classDeclaration: ClassDeclaration? = null
}