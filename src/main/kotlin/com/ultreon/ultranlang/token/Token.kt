package com.ultreon.ultranlang.token

class Token(var type: TokenType?, var value: Any?, val lineno: Int? = null, val column: Int? = null) {
    override fun toString() = "Token($type, $value, $lineno, $column)"
    fun repr() = toString()
}