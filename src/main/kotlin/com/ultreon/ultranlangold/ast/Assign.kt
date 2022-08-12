package com.ultreon.ultranlangold.ast

import com.ultreon.ultranlangold.token.Token

class Assign(var left: Var, var op: Token, var right: AST) : AST()