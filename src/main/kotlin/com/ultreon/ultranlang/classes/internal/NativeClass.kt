package com.ultreon.ultranlang.classes.internal

import com.ultreon.ultranlang.classes.*

class NativeClass() : ULClass("Class") {
    override val staticFields: List<ULField>
        get() = TODO("Not yet implemented")
    override val staticMethods: List<ULMethod>
        get() = TODO("Not yet implemented")
    override val staticMembers: List<ClassMember>
        get() = TODO("Not yet implemented")
    override val instanceFields: List<ULField>
        get() = TODO("Not yet implemented")
    override val instanceMethods: List<ULMethod>
        get() = TODO("Not yet implemented")
    override val instanceMembers: List<ClassMember>
        get() = TODO("Not yet implemented")
    override val constructors: List<ULConstructor>
        get() = TODO("Not yet implemented")

    override fun invoke(formalParams: List<ULObject>): ULObject {
        TODO("Not yet implemented")
    }

    override fun init() {
        TODO("Not yet implemented")
    }
}