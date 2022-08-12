package com.ultreon.ultranlangold.ast

import kotlin.String

abstract class AST {
    override fun toString(): String {
        return "<${this::class.simpleName}()>"
    }
}