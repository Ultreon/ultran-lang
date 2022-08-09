package com.ultreon.ultranlang.ast

open class ProcedureDecl(var procName: String, val formalParams: List<Param> /* a list of Param nodes */, var blockNode: Block, val native: Boolean = false) : AST {
    fun nativeCall(args: List<AST>): AST {
        throw IllegalStateException("Native call must be implemented in subclass")
    }
}