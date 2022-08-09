package com.ultreon.ultranlang.token

@Suppress("SpellCheckingInspection", "ClassName")
open class TokenType(val value: String) {

    // single-character token types
    object PLUS : TokenType("+")
    object MINUS : TokenType("-")
    object MUL : TokenType("*")
    object FLOAT_DIV : TokenType("/")
    object LPAREN : TokenType("(")
    object RPAREN : TokenType(")")
    object SEMI : TokenType(";")
    object DOT : TokenType(".")
    object COLON : TokenType(":")
    object COMMA : TokenType(",")

    // block of reserved words
    object PROGRAM : TokenType("PROGRAM")
    object INTEGER : TokenType("INTEGER")
    object REAL : TokenType("REAL")
    object STRING : TokenType("STRING")
    object BOOLEAN : TokenType("BOOLEAN")
    object TRUE : TokenType("TRUE")
    object FALSE : TokenType("FALSE")
    object INTEGER_DIV : TokenType("DIV")
    object VAR : TokenType("VAR")
    object FUNCTION : TokenType("FUNCTION")
    object BEGIN : TokenType("BEGIN")
    object END : TokenType("END")

    // misc
    object ID : TokenType("ID")
    object INTEGER_CONST : TokenType("INTEGER_CONST")
    object REAL_CONST : TokenType("REAL_CONST")
    object STRING_CONST : TokenType("INTEGER_CONST")
    object ASSIGN : TokenType("ASSIGN")
    object EOF : TokenType("EOF")

    override fun toString(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as TokenType

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    companion object {
        fun values(): Array<TokenType> {
            return arrayOf(PLUS, MINUS, MUL, FLOAT_DIV, LPAREN, RPAREN, SEMI, DOT, COLON, COMMA, PROGRAM, INTEGER, REAL,
                INTEGER_DIV, VAR, FUNCTION, BEGIN, END, ID, INTEGER_CONST, REAL_CONST, ASSIGN, EOF)
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
                "DOT" -> DOT
                "COLON" -> COLON
                "COMMA" -> COMMA
                "PROGRAM" -> PROGRAM
                "INTEGER" -> INTEGER
                "REAL" -> REAL
                "INTEGER_DIV" -> INTEGER_DIV
                "VAR" -> VAR
                "FUNCTION" -> FUNCTION
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