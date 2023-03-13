package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.symbol.ClassSymbol
import kotlin.String

class ValDecl(var varRefNode: VarRef, var typeNode: Type, override var classDeclaration: ClassDeclaration? = null, override val isStatic: Boolean = true) : LangObj(), ClassMemberDecl, FieldDecl {
    override var `this`: ClassSymbol? = null
    override val isConstant: Boolean = true
    override val name: String
        get() = varRefNode.value
}