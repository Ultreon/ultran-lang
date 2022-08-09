package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.token.Token

class Assign(var left: Var, var op: Token, var right: AST) : AST