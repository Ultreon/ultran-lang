package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.symbol.MethodSymbol
import com.ultreon.ultranlang.token.Token
import kotlin.String

class MethodCall(var funcName: String,
                 val actualParams: List<LangObj>,
                 var token: Token,
                 override val classDeclaration: ClassDeclaration) : LangObj(), ClassMemberDecl, Returnable {
    // a reference to procedure declaration symbol
    lateinit var methodSymbol: MethodSymbol
    override var child: Returnable? = null
    override var parent: Returnable? = null
}