package com.ultreon.ultranlang.classes

abstract class ScriptROProperty(override val name: String, override val isStatic: Boolean) : ClassMember {
    abstract fun get(): Any?
}