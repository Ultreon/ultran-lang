package com.ultreon.ultranlang.classes.internal

import com.ultreon.ultranlang.classes.PrimitiveObject

class PrimitiveByte(value: Byte) : PrimitiveObject<Byte>(value, Byte::class) {
    override fun castToLangObject() {
        TODO("Not yet implemented")
    }
}
