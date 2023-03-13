package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.token.Token
import kotlin.String

class VarRef(var token: Token) : LangObj(), Returnable {
    override var child: Returnable? = null
    override var parent: Returnable? = null
    var value = token.value as String
}