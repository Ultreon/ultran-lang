package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.token.Token

class Type(var token: Token) : LangObj() {
    var value = token.value
}