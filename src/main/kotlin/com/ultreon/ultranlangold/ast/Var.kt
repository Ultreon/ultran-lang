package com.ultreon.ultranlangold.ast

import com.ultreon.ultranlangold.token.Token

class Var(var token: Token) : AST() {
    var value = token.value
}