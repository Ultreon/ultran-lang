package com.ultreon.ultranlang

import com.google.gson.Gson
import com.ultreon.ultranlang.token.TokenType
import java.lang.System.err
import kotlin.properties.Delegates

/**
 * Build a dictionary of reserved keywords.
 *
 * The function relies on the fact that in the TokenType
 * enumeration the beginning of the block of reserved keywords is
 * marked with PROGRAM and the end of the block is marked with
 * the END keyword.
 *
 * Result:
 *     {'PROGRAM': <TokenType.PROGRAM: 'PROGRAM'>,
 *     'INTEGER': <TokenType.INTEGER: 'INTEGER'>,
 *     'REAL': <TokenType.REAL: 'REAL'>,
 *     'DIV': <TokenType.INTEGER_DIV: 'DIV'>,
 *     'VAR': <TokenType.VAR: 'VAR'>,
 *     'PROCEDURE': <TokenType.PROCEDURE: 'PROCEDURE'>,
 *     'BEGIN': <TokenType.BEGIN: 'BEGIN'>,
 *     'END': <TokenType.END: 'END'>}
 */
internal fun buildReservedKeywords(): HashMap<String, TokenType> {
    val ttList = TokenType.values()
    val startIndex = ttList.indexOf(TokenType.PROGRAM)
    val endIndex = ttList.indexOf(TokenType.END)
    val reservedKeywords = HashMap<String, TokenType>()
    for (i in startIndex..endIndex) {
        reservedKeywords[ttList[i].value] = ttList[i]
    }

    return reservedKeywords
}

val reservedKeywords = buildReservedKeywords()

var shouldLogScope by Delegates.notNull<Boolean>()
var shouldLogStack by Delegates.notNull<Boolean>()
var shouldLogTokens by Delegates.notNull<Boolean>()
var shouldLogInternalErrors by Delegates.notNull<Boolean>()

var logger = object : ILogger {
    override fun error(msg: Any?) {
        err.println(msg.toString())
    }

    override fun warn(msg: Any?) {
        err.println(msg.toString())
    }

    override fun info(msg: Any?) {
        println(msg.toString())
    }

    override fun debug(msg: Any?) {
        println(msg.toString())
    }
}

@Suppress("unused")
val productJson: ProductJson =
    Gson().fromJson(Script::class.java.getResourceAsStream("/product.json")!!.reader(), ProductJson::class.java)
