package com.ultreon.ultranlangold.ast

import com.ultreon.ultranlangold.symbol.FuncSymbol
import com.ultreon.ultranlangold.token.Token
import kotlin.String

class FuncCall(var procName: String, val actualParams: List<AST>, var token: Token) : AST() {
    // a reference to procedure declaration symbol
    var procSymbol: FuncSymbol? = null
}