package com.ultreon.ultranlang.classes.internal

import com.soywiz.kbignum.BigNum
import com.ultreon.ultranlang.classes.PrimitiveObject

class PrimitiveBigDec(value: BigNum) : PrimitiveObject<BigNum>(value, BigNum::class) {
    override fun castToLangObject() {
        TODO("Not yet implemented")
    }
}
