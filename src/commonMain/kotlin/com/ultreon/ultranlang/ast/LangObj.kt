package com.ultreon.ultranlang.ast

import kotlin.String

abstract class LangObj {
    override fun toString(): String {
        return "<${this::class.simpleName}()>"
    }
}