package com.ultreon.ultranlang.func

@Suppress("unused")
class ParamBuilder {
    internal val map = mutableMapOf<String, String>()

    fun add(name: String, type: String): ParamBuilder {
        map[name] = type
        return this
    }
}
