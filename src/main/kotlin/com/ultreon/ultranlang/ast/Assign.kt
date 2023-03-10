package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.token.Token

class Assign(var left: VarRef, var op: Token, var right: LangObj) : LangObj()