package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.token.Token

class Var(var token: Token) : AST() {
    var value = token.value
}