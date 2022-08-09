package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.symbol.ProcedureSymbol
import com.ultreon.ultranlang.token.Token

class ProcedureCall(var procName: String, val actualParams: List<AST>, var token: Token) : AST {
    // a reference to procedure declaration symbol
    var procSymbol: ProcedureSymbol? = null
}