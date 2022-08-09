package com.ultreon.ultranlang.symbol

import com.ultreon.ultranlang.ast.AST
import kotlin.reflect.KClass

class VarSymbol(name: String, type: Symbol?) : Symbol(name, type) {
    override fun toString(): String {
        return "<${this::class.qualifiedName}($name=$name, type=$type)>"
    }

    fun representation(): String = toString()
}