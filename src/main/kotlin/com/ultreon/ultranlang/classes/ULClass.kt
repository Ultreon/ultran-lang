package com.ultreon.ultranlang.classes

abstract class ULClass(val name: String) {
    abstract val staticFields: Map<String, ULField>
    abstract val staticMethods: Map<String, ULMethod>
    abstract val staticMembers: Map<String, ClassMember>

    abstract val instanceFields: Map<String, ULField>
    abstract val instanceMethods: Map<String, ULMethod>
    abstract val instanceMembers: Map<String, ClassMember>

    abstract val constructors: List<ULConstructor>
    abstract operator fun invoke(formalParams: List<ULObject>): ULObject

    fun buildFields(list: MutableMap<String, ULField>): Map<String, ULField> {
        return mapOf()
    }

    fun buildMethods(list: MutableMap<String, ULField>): Map<String, ULField> {
        return mapOf()
    }

    fun buildConstructors(list: MutableList<ULField>): List<ULField> {
        return listOf()
    }

    abstract fun init();
}

fun createMethod(name: String, isStatic: Boolean, block: (Array<ULObject>) -> Unit) {

}
