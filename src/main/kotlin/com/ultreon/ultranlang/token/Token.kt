package com.ultreon.ultranlang.token

class Token(var type: TokenType?, var value: Any?, val line: Int? = null, val column: Int? = null) {
    val locationIndented: String
        get() {
            val a = location
            return a + " ".repeat((10 - a.length).coerceAtLeast(0))
        }

    val location: String
        get() {
            return "$line:$column"
        }

    override fun toString() = "Token(${type}, ${value}, $line, $column)"
    fun repr(): String {
        return type?.repr().toString()
    }
}

fun String?.repr(): String {
    return when (this) {
        is String -> {
            val s = this
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\"", "\\\"")
            return "\"$s\""
        }

        else -> {
            toString().repr()
        }
    }
}
