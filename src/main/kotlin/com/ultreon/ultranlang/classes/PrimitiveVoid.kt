package com.ultreon.ultranlang.classes

object PrimitiveVoid : PrimitiveClass("void") {
    val instance: ULObject = object : ULObject(this) {
        override val `class`: ULClass
            get() = TODO("Not yet implemented")

    }

    override fun invoke(formalParams: List<ULObject>): ULObject {
        return instance
    }

    override fun init() {

    }
}