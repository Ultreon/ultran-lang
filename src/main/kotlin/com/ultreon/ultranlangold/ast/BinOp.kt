package com.ultreon.ultranlangold.ast

import com.ultreon.ultranlangold.token.Token

class BinOp(var left: AST, var op: Token, var right: AST) : AST()