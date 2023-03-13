package com.ultreon.ultranlang.classes.internal

import com.ultreon.ultranlang.classes.PrimitiveObject

class PrimitiveFloat(value: Float) : PrimitiveObject<Float>(value, Float::class) {
    override fun castToLangObject() {
        TODO("Not yet implemented")
    }
}
