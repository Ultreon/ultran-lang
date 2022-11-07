package com.ultreon.ultranlang.classes

abstract class ScriptObject {
    abstract fun getMembers(): List<ClassMember>
}
