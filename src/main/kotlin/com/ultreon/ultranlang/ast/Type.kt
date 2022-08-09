package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.token.Token

class Type(var token: Token) : AST() {
    var value = token.value
}