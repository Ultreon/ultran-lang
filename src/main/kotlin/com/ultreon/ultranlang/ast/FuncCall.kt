package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.symbol.FuncSymbol
import com.ultreon.ultranlang.token.Token
import kotlin.String

class FuncCall(var procName: String, val actualParams: List<AST>, var token: Token) : AST() {
    // a reference to procedure declaration symbol
    var procSymbol: FuncSymbol? = null
}