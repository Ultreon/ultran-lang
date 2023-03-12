package com.ultreon.ultranlang.classes

class ULClasses {
    private val classes = mutableMapOf<String, ULClass>()
    private val refs: MutableMap<String, ClassHolder> = mutableMapOf()

    fun loadDefaults() {

    }

    fun register(scriptClass: ULClass) {
        classes[scriptClass.name] = scriptClass
    }

    fun exists(name: String): Boolean {
        return name in refs
    }

    fun getRef(name: String): ClassHolder? {
        return refs[name]
    }

    operator fun get(name: String): ULClass? {
        return classes[name]
    }
}