package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.ScopedSymbolTable
import com.ultreon.ultranlang.symbol.ClassSymbol
import kotlin.String

class ClassDeclaration(var className: String) : LangObj() {
    lateinit var symbol: ClassSymbol
    lateinit var staticScope: ScopedSymbolTable
    lateinit var instanceScope: ScopedSymbolTable

    val classInit = ClassInitDecl()
    val staticFields = mutableListOf<FieldDecl>()
    val staticMethods = mutableListOf<MethodDeclaration>()
    val staticMembers = mutableListOf<ClassMemberDecl>()

    val constructors = mutableListOf<ConstructorDeclaration>()
    val instanceFields = mutableListOf<FieldDecl>()
    val instanceMethods = mutableListOf<MethodDeclaration>()
    val instanceMembers = mutableListOf<ClassMemberDecl>()
}