package com.ultreon.ultranlang.ast

import kotlin.String

class ClassDeclaration(var className: String) : AST() {
    val memberDecl = mutableListOf<ClassMemberDecl>()
}