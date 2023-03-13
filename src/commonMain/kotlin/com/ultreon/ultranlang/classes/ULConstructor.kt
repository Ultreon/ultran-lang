package com.ultreon.ultranlang.classes

abstract class ULConstructor(name: String) : ULMethod(name, false) {
    abstract override val paramTypes: List<ULClass>

    abstract override fun call(instance: ULObject?, params: Array<ULObject>): PrimitiveVoid
}