package com.ultreon.ultranlang

import com.ultreon.ultranlang.error.LexerException
import com.ultreon.ultranlang.token.Token
import com.ultreon.ultranlang.token.TokenType
import java.lang.RuntimeException

class Lexer(private val text: String) {
    var prevPos: Int = -1
        private set
    var pos = 0
    val currentChar: Char?
        get() {
            if (pos >= text.length) {
                return null
            }
            return text[pos]
        }
    var lineno = 1
    var column = 1

    fun error() {
        val s = "Lexer error on '$currentChar' at line $lineno, column $column"
        throw LexerException(message = s)
    }

    /**
     * Advance the `pos` pointer and set the `currentChar` property.
     */
    fun advance() {
        if (currentChar == '\n') {
            lineno++
            column = 0
        }

        pos++
        if (pos >= text.length) {
        } else {
            column++
        }
    }

    fun peek(): Char? {
        val peekPos = pos + 1
        return if (peekPos >= text.length) {
            null
        } else {
            text[peekPos]
        }
    }

    fun skipWhitespace() {
        while (currentChar != null && currentChar!!.isWhitespace() && currentChar != '\n') {
            advance()
        }
    }

    fun skipComment() {
        while (currentChar != ']') {
            advance()
        }
        advance()
    }

    fun string(): Token {
        var s = ""
        while (currentChar != '"') {
            if (currentChar == '\\') {
                advance()
                s += when (currentChar) {
                    'n' -> '\n'
                    't' -> '\t'
                    'r' -> '\r'
                    'b' -> '\b'
                    '0' -> '\u0000'
                    'x' -> {
                        advance()
                        advance()
                        val hex = text.substring(pos - 2, pos)
                        s += hex.toInt(16).toChar()
                    }

                    'u' -> {
                        advance()
                        advance()
                        advance()
                        advance()
                        val hex = text.substring(pos - 4, pos)
                        s += hex.toInt(16).toChar()
                    }

                    else -> currentChar
                }
                s += currentChar
            } else if (currentChar != '"') {
                s += currentChar
            } else {
                break
            }
            advance()
        }
        advance()
        return Token(TokenType.STRING_CONST, s, lineno, column)
    }

    /**
     * Return a (multi-digit) integer or float consumed from the input.
     */
    fun number(): Token {
        // Create a new token with current line and column number
        val token = Token(null, null, lineno, column)

        var result = ""
        while (currentChar != null && currentChar!!.isDigit()) {
            result += currentChar!!
            advance()
        }

        if (currentChar == '.') {
            result += currentChar!!
            advance()

            while (currentChar != null && currentChar!!.isDigit()) {
                result += currentChar!!
                advance()
            }

            token.type = TokenType.REAL_CONST
            token.value = result.toDouble()
        } else {
            token.type = TokenType.INTEGER_CONST
            token.value = result.toInt()
        }

        return token
    }

    /**
     * Handle identifiers and reserved keywords
     */
    internal fun id(): Token {
        // Create a new token with current line and column number
        val token = Token(null, null, lineno, column)

        var value = ""
        do {
            value += currentChar!!
            advance()
        } while (currentChar != null && currentChar!!.isLetterOrDigit() && !currentChar!!.isWhitespace())

        val tokenType = reservedKeywords[value.uppercase()]
        if (tokenType == null) {
            token.type = TokenType.ID
            token.value = value
        } else {
            token.type = tokenType
            token.value = value.uppercase()
        }

        return token
    }

    /**
     * Lexical analyzer (also known as scanner or tokenizer)
     *
     * This method is responsible for breaking a sentence
     * apart into tokens. One token at a time.
     */
    fun getNextToken(): Token {
        prevPos = pos
        while (currentChar != null) {
            if (currentChar!!.isWhitespace() && currentChar != '\n') {
                skipWhitespace()
                continue
            }

            if (currentChar == '[') {
                advance()
                skipComment()
                continue
            }

            if (currentChar == '"') {
                advance()
                return string()
            }

            if (currentChar!!.isLetter()) {
                return id()
            }

            if (currentChar!!.isDigit()) {
                return number()
            }

            if (currentChar == '=') {
                val token = Token(
                    TokenType.ASSIGN,
                    TokenType.ASSIGN.value, // ":="
                    lineno,
                    column
                )

                advance()
                return token
            }

            val tokenType: TokenType
            try {
                tokenType = TokenType(currentChar!!.toString())
            } catch (e: IllegalArgumentException) {
                error()
                throw IllegalStateException("Unreachable")
            }

            val token = Token(tokenType, tokenType.value, lineno, column)
            advance()
            return token
        }

        return Token(TokenType.EOF, TokenType.EOF.value)
    }
}