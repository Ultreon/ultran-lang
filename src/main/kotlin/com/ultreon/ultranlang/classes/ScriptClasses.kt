package com.ultreon.ultranlang.classes

class ScriptClasses {
    private val classes = mutableMapOf<String, ScriptClass>()
    private val refs: MutableMap<String, ClassRef> = mutableMapOf()

    fun loadDefaults() {

    }

    fun register(scriptClass: ScriptClass) {
        classes[scriptClass.name] = scriptClass
    }

    fun exists(name: String): Boolean {
        return name in refs
    }

    fun getRef(name: String): ClassRef? {
        return refs[name]
    }

    operator fun get(name: String): ScriptClass? {
        return classes[name]
    }
}