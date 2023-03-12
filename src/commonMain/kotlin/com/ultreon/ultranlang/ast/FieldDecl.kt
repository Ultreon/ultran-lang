package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.symbol.ClassSymbol

interface FieldDecl : ClassMemberDecl {
    val isStatic: Boolean
    var `this`: ClassSymbol?
}
