package com.ultreon.ultranlang.classes.internal

import com.ultreon.ultranlang.classes.PrimitiveObject

class PrimitiveShort(value: Short) : PrimitiveObject<Short>(value, Short::class) {
    override fun castToLangObject() {
        TODO("Not yet implemented")
    }
}
