package com.ultreon.ultranlang.classes

open class ULObject(val `class`: ULClass) {
    val fields: Map<String, ClassMember> = `class`.instanceFields
    val methods: Map<String, ClassMember> = `class`.instanceMethods
    val members: Map<String, ClassMember> = `class`.instanceMembers
}
