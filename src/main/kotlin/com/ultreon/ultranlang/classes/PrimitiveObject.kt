package com.ultreon.ultranlang.classes

import com.ultreon.ultranlang.Runtime
import kotlin.reflect.KClass

open class PrimitiveObject<T : Any>(clazz: KClass<T>) : ULObject(Runtime.getPrimitiveClass(clazz)) {

}
