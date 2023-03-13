package com.ultreon.ultranlang.classes

abstract class ULClass(val name: String) {
    abstract val staticFields: List<ULField>
    abstract val staticMethods: List<ULMethod>
    abstract val staticMembers: List<ClassMember>
    abstract val instanceFields: List<ULField>
    abstract val instanceMethods: List<ULMethod>
    abstract val instanceMembers: List<ClassMember>
    abstract val constructors: List<ULConstructor>
    abstract operator fun invoke(arguments: List<ULObject>): ULObject

    fun buildFields(static: MutableList<ULField>, instance: MutableList<ULField>) {

    }

    fun buildMethods(static: MutableList<ULMethod>, instance: MutableList<ULMethod>) {

    }

    fun buildConstructors(static: MutableList<ULConstructor>, instance: MutableList<ULConstructor>) {

    }

    abstract fun init()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ULClass

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    abstract fun getConstructor(formalTypes: List<ULClass>): ULConstructor?
    abstract fun getStaticMethod(name: String, formalTypes: List<ULClass>): ULMethod?
    abstract fun getInstanceMethod(name: String, formalTypes: List<ULClass>): ULMethod?
    abstract fun getStaticField(name: String): ULField?
    abstract fun getInstanceField(name: String): ULField?
}

fun createMethod(name: String, isStatic: Boolean, block: (Array<ULObject>) -> Unit) {

}
