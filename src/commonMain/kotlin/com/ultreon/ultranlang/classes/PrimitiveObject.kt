package com.ultreon.ultranlang.classes

import com.ultreon.ultranlang.Runtime
import kotlin.reflect.KClass

abstract class PrimitiveObject<T : Any>(val value: T, clazz: KClass<T>) : ULObject(Runtime.getPrimitiveClass(clazz)) {
    abstract fun castToLangObject()
}
