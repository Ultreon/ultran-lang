package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.token.Token

class BinOp(var left: LangObj, var op: Token, var right: LangObj) : LangObj()