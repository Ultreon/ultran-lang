package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.token.Token

class UnaryOp(var op: Token, var expr: LangObj) : LangObj() {
    var token = op
}