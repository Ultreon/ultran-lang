package com.ultreon.ultranlangold.ast

import com.ultreon.ultranlangold.token.Token

class UnaryOp(var op: Token, var expr: AST) : AST() {
    var token = op
}