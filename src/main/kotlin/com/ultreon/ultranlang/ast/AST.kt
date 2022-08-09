package com.ultreon.ultranlang.ast

import kotlin.String

abstract class AST {
    override fun toString(): String {
        return "<${this::class.simpleName}()>"
    }
}