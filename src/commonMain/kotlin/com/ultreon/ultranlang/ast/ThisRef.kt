package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.classes.ClassHolder
import com.ultreon.ultranlang.symbol.ClassSymbol
import com.ultreon.ultranlang.token.Token

class ThisRef(val classDeclaration: ClassDeclaration, var token: Token) : LangObj(), Returnable {
    lateinit var obj: ClassSymbol
    override var child: Returnable? = null
    override var parent: Returnable? = null
    var classRef: ClassHolder? = null
    var value = token.value
}