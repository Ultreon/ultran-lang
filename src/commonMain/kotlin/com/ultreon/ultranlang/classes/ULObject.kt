package com.ultreon.ultranlang.classes

open class ULObject(val `class`: ULClass) {
    fun getMethod(name: String, map: List<ULClass>): ULMethod? {
        return `class`.getInstanceMethod(name, map)
    }

    fun getField(name: String): ULField? {
        return `class`.getInstanceField(name)
    }

    val fields: List<ClassMember> = `class`.instanceFields
    val methods: List<ClassMember> = `class`.instanceMethods
    val members: List<ClassMember> = `class`.instanceMembers
}
