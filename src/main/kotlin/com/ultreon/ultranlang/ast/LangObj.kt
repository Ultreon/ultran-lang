package com.ultreon.ultranlang.ast

import com.ultreon.ultranlang.symbol.ClassSymbol
import kotlin.String

abstract class LangObj {
    override fun toString(): String {
        return "<${this::class.simpleName}()>"
    }
}