package com.ultreon.ultranlang.symbol

import com.ultreon.ultranlang.ast.AST
import com.ultreon.ultranlang.ast.Block
import com.ultreon.ultranlang.ast.Param
import kotlin.reflect.KClass

class ProcedureSymbol(name: String, formalParams: List<VarSymbol>? = null) : Symbol(name) {
    lateinit var blockAst: Block
    val formalParams: MutableList<VarSymbol> = formalParams?.toMutableList() ?: mutableListOf()

    override fun toString(): String {
        return "<${this::class.qualifiedName}($name=$name, type=$type)>"
    }

    fun representation(): String = toString()
}