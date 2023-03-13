package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.symbol.ClassSymbol
import kotlin.String

interface FieldDecl : ClassMemberDecl {
    val isStatic: Boolean
    var `this`: ClassSymbol?
    val isConstant: Boolean
    val name: String
}
