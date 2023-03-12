package com.ultreon.ultranlang

import com.ultreon.ultranlang.classes.*
import kotlin.reflect.KClass

object Runtime {
    private val primitives: MutableMap<KClass<out Any>, PrimitiveObject<Any>> = mutableMapOf()
    lateinit var classes: ULClasses private set

    fun init(launch: LaunchProperties) {
        classes = ULClasses()
    }

    fun crash(t: Throwable) {
        TODO("Not yet implemented")
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getPrimitiveClass(clazz: KClass<T>): PrimitiveObject<T> {
        return primitives[clazz] as PrimitiveObject<T>
    }

    private fun <T : Any> createPrimitiveClass(name: String, clazz: KClass<T>): PrimitiveClass<T> {
        return object : PrimitiveClass<T>(name) {
            override fun createObject(langObj: T): ULObject {
                TODO("Not yet implemented")
            }

            override fun init() {
                TODO("Not yet implemented")
            }

        }
    }
}