package com.ultreon.ultranlang.classes.internal

import com.ultreon.ultranlang.classes.PrimitiveObject

class PrimitiveLong(value: Long) : PrimitiveObject<Long>(value, Long::class) {
    override fun castToLangObject() {
        TODO("Not yet implemented")
    }
}
