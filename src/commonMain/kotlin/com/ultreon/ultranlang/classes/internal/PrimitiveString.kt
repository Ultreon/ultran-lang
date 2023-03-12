package com.ultreon.ultranlang.classes.internal

import com.ultreon.ultranlang.classes.PrimitiveClass
import com.ultreon.ultranlang.classes.PrimitiveObject
import com.ultreon.ultranlang.classes.ULObject

class PrimitiveString(value: String) : PrimitiveObject<String>(value, String::class) {
    override fun castToLangObject() {
        TODO("Not yet implemented")
    }
}
