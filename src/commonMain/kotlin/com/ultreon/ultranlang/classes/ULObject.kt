package com.ultreon.ultranlang.classes

open class ULObject(val `class`: ULClass) {
    val fields: List<ClassMember> = `class`.instanceFields
    val methods: List<ClassMember> = `class`.instanceMethods
    val members: List<ClassMember> = `class`.instanceMembers
}
