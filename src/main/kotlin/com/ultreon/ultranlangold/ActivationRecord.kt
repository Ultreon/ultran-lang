package com.ultreon.ultranlangold

class ActivationRecord(var name: String, var type: ARType, var nestingLevel: Int) {
    val members = mutableMapOf<String, Any?>()

    operator fun set(key: String, value: Any?) {
        members[key] = value
    }

    operator fun get(key: String): Any? {
        return members[key]
    }

    override fun toString(): String {
        val lines = mutableListOf(
            "$nestingLevel: ${type.name} $name"
        )
        for ((name, value) in members) {
            lines += "    ${name.padEnd(20)}: $value"
        }

        return lines.joinToString("\n")
    }

    fun representation(): String{
        return toString()
    }
}
