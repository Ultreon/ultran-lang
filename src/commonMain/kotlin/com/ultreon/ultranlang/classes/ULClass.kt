package com.ultreon.ultranlang.classes

abstract class ULClass(val name: String) {
    abstract val staticFields: List<ULField>
    abstract val staticMethods: List<ULMethod>
    abstract val staticMembers: List<ClassMember>
    abstract val instanceFields: List<ULField>
    abstract val instanceMethods: List<ULMethod>
    abstract val instanceMembers: List<ClassMember>
    abstract val constructors: List<ULConstructor>
    abstract operator fun invoke(formalParams: List<ULObject>): ULObject

    fun buildFields(static: MutableList<ULField>, instance: MutableList<ULField>) {

    }

    fun buildMethods(static: MutableList<ULMethod>, instance: MutableList<ULMethod>) {

    }

    fun buildConstructors(static: MutableList<ULConstructor>, instance: MutableList<ULConstructor>) {

    }

    abstract fun init();
}

fun createMethod(name: String, isStatic: Boolean, block: (Array<ULObject>) -> Unit) {

}
