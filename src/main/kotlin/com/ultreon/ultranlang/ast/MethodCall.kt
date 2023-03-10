package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.symbol.FuncSymbol
import com.ultreon.ultranlang.symbol.MethodSymbol
import com.ultreon.ultranlang.token.Token
import kotlin.String

class MethodCall(var funcName: String, val actualParams: List<LangObj>, var token: Token) : LangObj() {
    // a reference to procedure declaration symbol
    lateinit var methodSymbol: MethodSymbol
}