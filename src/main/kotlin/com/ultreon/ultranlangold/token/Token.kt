package com.ultreon.ultranlangold.token

class Token(var type: TokenType?, var value: Any?, val line: Int? = null, val column: Int? = null) {
    override fun toString() = "Token($type, $value, $line, $column)"
    fun repr() = toString()
}