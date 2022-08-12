package com.ultreon.ultranlangold.annotations

import com.ultreon.ultranlangold.ast.AST
import kotlin.reflect.KClass

annotation class Visit(val value: KClass<out AST>)