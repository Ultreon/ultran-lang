package com.ultreon.ultranlang.ast

import kotlin.String

class ClassDeclaration(var className: String) : LangObj() {
    val members = mutableListOf<ClassMemberDecl>()
    val fields = mutableListOf<FieldDecl>()
    val methods = mutableListOf<MethodDeclaration>()
    val constructors = mutableListOf<ConstructorDeclaration>()
    val classInit = ClassInitDecl()
}