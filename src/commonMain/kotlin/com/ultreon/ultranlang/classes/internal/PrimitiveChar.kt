package com.ultreon.ultranlang.classes.internal

import com.ultreon.ultranlang.classes.PrimitiveObject

class PrimitiveChar(value: Char) : PrimitiveObject<Char>(value, Char::class) {
    override fun castToLangObject() {
        TODO("Not yet implemented")
    }
}
