package com.ultreon.ultranlang

import com.ultreon.ultranlang.error.LexerException
import com.ultreon.ultranlang.token.Token
import com.ultreon.ultranlang.token.TokenType
import java.lang.IllegalStateException

class Lexer(private val text: String) {
    var pos = 0
    var currentChar: Char? = text[pos]
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
            currentChar = null
        } else {
            currentChar = text[pos]
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
        while (currentChar != null && currentChar!!.isWhitespace()) {
            advance()
        }
    }

    fun skipComment() {
        while (currentChar != '}') {
            advance()
        }
        advance()
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
        while (currentChar != null && currentChar!!.isLetterOrDigit()) {
            value += currentChar!!
            advance()
        }

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
        while (currentChar != null) {
            if (currentChar!!.isWhitespace()) {
                skipWhitespace()
                continue
            }

            if (currentChar == '{') {
                advance()
                skipComment()
                continue
            }

            if (currentChar!!.isLetter()) {
                return id()
            }

            if (currentChar!!.isDigit()) {
                return number()
            }

            if (currentChar == ':' && peek() == '=') {
                val token = Token(
                    TokenType.ASSIGN,
                    TokenType.ASSIGN.value, // ":="
                    lineno,
                    column
                )

                advance()
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