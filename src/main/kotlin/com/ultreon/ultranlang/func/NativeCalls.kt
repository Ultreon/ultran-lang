package com.ultreon.ultranlang.func

import com.ultreon.ultranlang.ActivationRecord
import com.ultreon.ultranlang.symbol.BuiltinTypeSymbol
import com.ultreon.ultranlang.symbol.FuncSymbol
import com.ultreon.ultranlang.symbol.VarSymbol

object NativeCalls {
    private val symbols = mutableMapOf<String, FuncSymbol>()
    private val declarations = mutableMapOf<String, (ActivationRecord) -> Any?>()

    fun nativeCall(func: FuncSymbol, args: List<VarSymbol>, ar: ActivationRecord): Any? {
        val procName = func.name
        if (declarations.containsKey(procName)) {
            return declarations[procName]!!(ar)
        } else {
            throw IllegalArgumentException("Native procedure $procName not found")
        }
    }

    fun exists(name: String): Boolean {
        return declarations.containsKey(name)
    }

    fun load() {
        register("print", mutableMapOf(Pair("message", BuiltinTypeSymbol.STRING))) { args ->
            val message = args["message"]
            if (message is String) {
                println(message)
            } else {
                println(message)
            }
        }
    }

    private fun register(name: String, params: Map<String, String>, func: (ActivationRecord) -> Any?) {
        symbols[name] =
            FuncSymbol(name, params.entries.toList().map { VarSymbol("message", BuiltinTypeSymbol("INTEGER")) })
        declarations[name] = func
    }

    operator fun get(name: String): FuncSymbol? {
        return symbols[name]
    }
}
