package com.ultreon.ultranlang.func

@Suppress("unused")
class ParamBuilder {
    internal val map = mutableMapOf<String, String>()

    fun add(name: String, type: String) {
        map[name] = type
    }
}
