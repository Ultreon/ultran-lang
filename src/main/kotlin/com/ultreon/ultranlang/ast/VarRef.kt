package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.token.Token

class VarRef(var token: Token) : LangObj(), Returnable {
    var child: Returnable? = null
    var value = token.value
}