package com.ultreon.ultranlang.annotations

import com.ultreon.ultranlang.ast.LangObj
import kotlin.reflect.KClass

annotation class Visit(val value: KClass<out LangObj>) {
}