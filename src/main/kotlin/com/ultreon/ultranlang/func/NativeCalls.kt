package com.ultreon.ultranlang.func

import com.ultreon.ultranlang.ActivationRecord
import com.ultreon.ultranlang.params
import com.ultreon.ultranlang.symbol.BuiltinTypeSymbol
import com.ultreon.ultranlang.symbol.FuncSymbol
import com.ultreon.ultranlang.symbol.VarSymbol
import javax.script.ScriptException
import kotlin.random.Random
import kotlin.random.nextInt

class NativeCalls {
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

    fun loadDefaults() {
        register("print", params().add("message", BuiltinTypeSymbol.STRING)) { args ->
            val message = args["message"]
            if (message is String) {
                println(message)
            } else {
                println(message)
            }
        }
        register(
            "randInt",
            params().add("x", BuiltinTypeSymbol.INTEGER).add("y", BuiltinTypeSymbol.INTEGER)
        ) { args ->
            val x = args["x"]
            val y = args["y"]

            if (x is Int && y is Int) {
                return@register Random.nextInt(x..y)
            } else {
                throw ScriptException("randInt expects two integers")
            }
        }
    }

    fun register(name: String, params: ParamBuilder, func: (ActivationRecord) -> Any?) {
        this.register(name, params.map, func)
    }

    fun register(name: String, params: Map<String, String>, func: (ActivationRecord) -> Any?) {
        symbols[name] =
            FuncSymbol(name, params.entries.toList().map { VarSymbol(it.key, BuiltinTypeSymbol(it.value)) }, this)
        declarations[name] = func
    }

    operator fun get(name: String): FuncSymbol? {
        return symbols[name]
    }
}
