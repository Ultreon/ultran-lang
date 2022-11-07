package com.ultreon.ultranlang.classes

abstract class ScriptRWProperty(name: String, isStatic: Boolean) : ScriptROProperty(name, isStatic) {
    abstract fun set(value: Any?)
}