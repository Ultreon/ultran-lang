package com.ultreon.ultranlang.classes

import com.ultreon.ultranlang.ExecutionException
import com.ultreon.ultranlang.Runtime
import com.ultreon.ultranlang.classes.internal.PrimitiveString

abstract class PrimitiveClass<T>(value: String): ULClass(value) {
    override val staticFields: List<ULField> = listOf()
    override val staticMethods: List<ULMethod> = listOf()
    override val staticMembers: List<ClassMember> = listOf()
    override val instanceFields: List<ULField> = listOf()
    override val instanceMethods: List<ULMethod> = listOf()
    override val instanceMembers: List<ClassMember> = listOf()
    override val constructors: List<ULConstructor> = listOf()

    abstract fun createObject(langObj: T): ULObject

    override fun invoke(formalParams: List<ULObject>): ULObject {
        try {
            throw ExecutionException(Runtime.classes["ultran/lang/VMInternalError"]!!(listOf(
                PrimitiveString("Can't instantiate a primitive class.")
            )))
        } catch (t: Throwable) {
            Runtime.crash(t)
            throw Error("Internal UltranLang crash. Report at $")
        }
    }

}
