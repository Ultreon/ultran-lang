package com.ultreon.ultranlang.classes.internal

import com.soywiz.kbignum.BigInt
import com.ultreon.ultranlang.classes.PrimitiveObject

class PrimitiveBigInt(value: BigInt) : PrimitiveObject<BigInt>(value, BigInt::class) {
    override fun castToLangObject() {
        TODO("Not yet implemented")
    }
}
