package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.symbol.ClassSymbol
import com.ultreon.ultranlang.symbol.FuncSymbol
import com.ultreon.ultranlang.token.Token
import kotlin.String

class FuncCall(var funcName: String, val actualParams: List<LangObj>, var token: Token) : LangObj(), Returnable {
    var classSymbol: ClassSymbol? = null // Only for method & constructor calls.

    // a reference to procedure declaration symbol
    var funcSymbol: FuncSymbol? = null
    override var child: Returnable? = null
    override var parent: Returnable? = null
}