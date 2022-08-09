package com.ultreon.ultranlang

import com.ultreon.ultranlang.symbol.BuiltinTypeSymbol
import com.ultreon.ultranlang.symbol.Symbol

class ScopedSymbolTable(val scopeName: String, val scopeLevel: Int, val enclosingScope: ScopedSymbolTable? = null) {
    internal val symbols = mutableMapOf<String, Symbol>()

    internal fun initBuiltins() {
        insert(BuiltinTypeSymbol("INTEGER"))
        insert(BuiltinTypeSymbol("REAL"))
    }

    override fun toString(): String {
        val h1 = "SCOPE (SCOPED SYMBOL TABLE)"
        val lines = mutableListOf<String>("\n", h1, "=".repeat(h1.length))

        for ((headerName, headerValue) in mutableMapOf(
            Pair("Scope name", scopeName),
            Pair("Scope level", scopeLevel),
            Pair("Enclosing scope", enclosingScope?.scopeName)
        )) {
            lines.add("${headerName.padEnd(15, ' ')}: $headerValue")
        }

        val h2 = "Scope (Scoped symbol table) contents"
        lines.addAll(listOf(h2, "-".repeat(h2.length)))

        for ((name, symbol) in symbols) {
            lines.add("${name.padEnd(7, ' ')}: $symbol")
        }

        lines.add("\n")

        return "\n" + lines.joinToString("\n")
    }

    fun representation(): String = toString()

    fun log(msg: String) {
        if (SHOULD_LOG_SCOPE) {
            println(msg)
        }
    }

    fun insert(symbol: Symbol) {
        log("Insert: ${symbol.name}")
        symbol.scopeLevel = scopeLevel
        symbols[symbol.name] = symbol
    }

    fun lookup(name: String, currentScopeOnly: Boolean = false): Symbol? {
        log("Lookup: $name. (Scope name: $scopeName)")
        // "symbol" is either an instance of the Symbol class or null
        val symbol = symbols[name]

        if (symbol != null) {
            return symbol
        }

        if (currentScopeOnly) {
            return null
        }

        if (enclosingScope != null) {
            return enclosingScope.lookup(name)
        }

        return null
    }
}