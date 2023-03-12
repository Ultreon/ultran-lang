package com.ultreon.ultranlang

import com.ultreon.ultranlang.ast.LangObj
import kotlin.reflect.KClass

open class NodeVisitor {
    var members: MutableMap<KClass<out Any>, (Any) -> Any?> = hashMapOf()

    fun visit(node: LangObj): Any? {
        for ((clazz, visitor) in members) {
            if (clazz.isInstance(node)) {
                return visitor(node)
            }
        }
        throw IllegalStateException("No visitor registered for ${node::class.qualifiedName}")
    }

    inline operator fun <reified T> set(clazz: KClass<T & Any>, crossinline visitor: (T) -> Any?) {
        members[clazz] = wrap@{
            if (it is T) {
                return@wrap visitor(it)
            }
            throw Error("What happened?")
        }
    }
}