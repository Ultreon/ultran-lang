package com.ultreon.ultranlangold.ast

import com.ultreon.ultranlangold.token.Token

class String(var token: Token) : AST() {
    var value = token.value
}