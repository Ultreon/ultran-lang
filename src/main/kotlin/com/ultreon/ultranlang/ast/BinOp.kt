package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.token.Token

class BinOp(var left: AST, var op: Token, var right: AST) : AST()