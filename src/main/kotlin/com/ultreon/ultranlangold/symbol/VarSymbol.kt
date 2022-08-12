package com.ultreon.ultranlangold.symbol

class VarSymbol(name: String, type: Symbol?) : Symbol(name, type) {
    override fun toString(): String {
        return "<${this::class.simpleName}(name=$name, type=$type)>"
    }

    fun representation(): String = toString()
}