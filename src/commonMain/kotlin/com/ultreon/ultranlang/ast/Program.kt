package com.ultreon.ultranlang.ast

import kotlin.String

class Program(var name: String) : LangObj() {
    var statements = mutableListOf<LangObj>()
}