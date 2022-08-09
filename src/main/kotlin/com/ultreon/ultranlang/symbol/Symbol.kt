package com.ultreon.ultranlang.symbol

import com.ultreon.ultranlang.ast.AST
import kotlin.reflect.KClass

abstract class Symbol(val name: String, val type: Symbol? = null) {
    var scopeLevel = 0
}