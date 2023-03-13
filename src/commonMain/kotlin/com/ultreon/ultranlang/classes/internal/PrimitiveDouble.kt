package com.ultreon.ultranlang.classes.internal

import com.ultreon.ultranlang.classes.PrimitiveObject

class PrimitiveDouble(value: Double) : PrimitiveObject<Double>(value, Double::class) {
    override fun castToLangObject() {
        TODO("Not yet implemented")
    }
}
