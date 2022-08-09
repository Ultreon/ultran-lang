package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.token.Token

class UnaryOp(var op: Token, var expr: AST) : AST {
    var token = op
}