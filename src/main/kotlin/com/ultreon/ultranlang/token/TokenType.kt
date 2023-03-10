package com.ultreon.ultranlang.token

@Suppress("SpellCheckingInspection")
class TokenType(val value: String) {
    override fun toString(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as TokenType

        if (value != other.value) return false

        return true
    }

    val isNewline: Boolean get() = this == SEMI || this == NEW_LINE

    override fun hashCode(): Int {
        return value.hashCode()
    }

    companion object {
        // single-character token types
        val PLUS = TokenType("+")
        val MINUS = TokenType("-")
        val MUL = TokenType("*")
        val FLOAT_DIV = TokenType("/")
        val LPAREN = TokenType("(")
        val RPAREN = TokenType(")")
        val LCURL = TokenType("{")
        val RCURL = TokenType("}")
        val SEMI = TokenType(";")
        val DOT = TokenType(".")
        val COLON = TokenType(":")
        val COMMA = TokenType(",")
        val NEW_LINE = TokenType("\n")

        // block of reserved words
        val PROGRAM = TokenType("PROGRAM")
        val INTEGER = TokenType("INTEGER")
        val REAL = TokenType("REAL")
        val STRING = TokenType("STRING")
        val BOOLEAN = TokenType("BOOLEAN")
        val TRUE = TokenType("TRUE")
        val FALSE = TokenType("FALSE")
        val INTEGER_DIV = TokenType("DIV")
        val VAR = TokenType("VAR")
        val VAL = TokenType("VAL")
        val FUNCTION = TokenType("FUNCTION")
        val CLASS = TokenType("CLASS")
        val CONSTRUCTOR = TokenType("CONSTRUCTOR")
        val STATIC = TokenType("STATIC")
        val BEGIN = TokenType("BEGIN")
        val END = TokenType("END")
        val RETURN = TokenType("RETURN")

        // misc
        val ID = TokenType("ID")
        val INTEGER_CONST = TokenType("INTEGER_CONST")
        val REAL_CONST = TokenType("REAL_CONST")
        val STRING_CONST = TokenType("INTEGER_CONST")
        val ASSIGN = TokenType("ASSIGN")
        val EOF = TokenType("EOF")

        fun values(): Array<TokenType> {
            return arrayOf(PLUS, MINUS, MUL, FLOAT_DIV, LPAREN, RPAREN, LCURL, RCURL, SEMI, NEW_LINE, DOT, COLON, COMMA, PROGRAM,
                INTEGER, REAL,
                INTEGER_DIV, VAR, FUNCTION, BEGIN, END, ID, INTEGER_CONST, STRING_CONST, REAL_CONST, ASSIGN, EOF)
        }

        fun valueOf(value: String): TokenType {
            return when (value) {
                "PLUS" -> PLUS
                "MINUS" -> MINUS
                "MUL" -> MUL
                "FLOAT_DIV" -> FLOAT_DIV
                "LPAREN" -> LPAREN
                "RPAREN" -> RPAREN
                "SEMI" -> SEMI
                "NEW_LINE" -> NEW_LINE
                "DOT" -> DOT
                "COLON" -> COLON
                "COMMA" -> COMMA
                "PROGRAM" -> PROGRAM
                "INTEGER" -> INTEGER
                "REAL" -> REAL
                "INTEGER_DIV" -> INTEGER_DIV
                "VAR" -> VAR
                "FUNCTION" -> FUNCTION
                "CLASS" -> CLASS
                "CONSTRUCTOR" -> CONSTRUCTOR
                "STATIC" -> STATIC
                "BEGIN" -> BEGIN
                "END" -> END
                "ID" -> ID
                "INTEGER_CONST" -> INTEGER_CONST
                "REAL_CONST" -> REAL_CONST
                "ASSIGN" -> ASSIGN
                "EOF" -> EOF
                else -> throw IllegalArgumentException("No object com.ultreon.ultranlang.TokenType.$value")
            }
        }
    }
}