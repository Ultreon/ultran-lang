package com.ultreon.ultranlang.symbol

abstract class Symbol(val name: String, val type: Symbol? = null) {
    var scopeLevel = 0

    override fun toString(): String {
        return "<${this::class.simpleName} name=$name, type=$type>"
    }
}