package com.ultreon.ultranlang.symbol

class BuiltinTypeSymbol(name: String) : Symbol(name) {
    override fun toString(): String {
        return name
    }

    fun representation(): String {
        return "<${this::class.qualifiedName}(name=$name)>"
    }

    companion object {
        const val INTEGER: String = "INTEGER"
        const val REAL: String = "REAL"
        const val STRING: String = "STRING"
    }
}