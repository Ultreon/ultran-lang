package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.classes.ClassHolder
import com.ultreon.ultranlang.token.Token

class ThisRef(var token: Token) : LangObj(), Returnable {
    override var child: Returnable? = null
    var classRef: ClassHolder? = null
    var value = token.value
}