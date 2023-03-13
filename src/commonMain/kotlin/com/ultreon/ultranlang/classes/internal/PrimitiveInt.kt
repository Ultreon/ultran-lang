package com.ultreon.ultranlang.classes.internal

import com.ultreon.ultranlang.classes.PrimitiveObject

class PrimitiveInt(value: Int) : PrimitiveObject<Int>(value, Int::class) {
    override fun castToLangObject() {
        TODO("Not yet implemented")
    }
}
