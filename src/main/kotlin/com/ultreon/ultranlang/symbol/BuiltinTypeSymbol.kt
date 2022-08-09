package com.ultreon.ultranlang.symbol

import com.ultreon.ultranlang.ast.AST
import kotlin.reflect.KClass

class BuiltinTypeSymbol(name: String) : Symbol(name) {
    override fun toString(): String {
        return name
    }

    fun representation(): String {
        return "<${this::class.qualifiedName}(name=$name)>"
    }
}