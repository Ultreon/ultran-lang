package com.ultreon.ultranlang;

import com.ultreon.ultranlang.func.NativeCalls;
import com.ultreon.ultranlang.symbol.BuiltinTypeSymbol;
import com.ultreon.ultranlang.symbol.FuncSymbol;
import com.ultreon.ultranlang.symbol.Symbol;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScopedSymbolTable implements Repr {
    private final String scopeName;
    private final int scopeLevel;
    private final ScopedSymbolTable enclosingScope;
    private final Map<String, Symbol> symbols = new HashMap<>();

    public ScopedSymbolTable(String scopeName, int scopeLevel, ScopedSymbolTable enclosingScope) {
        this.scopeName = scopeName;
        this.scopeLevel = scopeLevel;
        this.enclosingScope = enclosingScope;
    }

    public String getScopeName() {
        return scopeName;
    }

    public int getScopeLevel() {
        return scopeLevel;
    }

    public ScopedSymbolTable getEnclosingScope() {
        return enclosingScope;
    }

    void initBuiltins() {
        insert(new BuiltinTypeSymbol(BuiltinTypeSymbol.INTEGER));
        insert(new BuiltinTypeSymbol(BuiltinTypeSymbol.REAL));
        insert(new BuiltinTypeSymbol(BuiltinTypeSymbol.STRING));
    }

    public String toString() {
        String h1 = "SCOPE (SCOPE SYMBOL TABLE)";
        List<String> lines = new ArrayList<>(List.of("\n", h1, "=".repeat(h1.length())));

        Map<String, Object> map = new HashMap<>();
        map.put("Scope name", scopeName);
        map.put("Scope level", scopeLevel);
        map.put("Enclosing scope", enclosingScope == null ? null : enclosingScope.getScopeName());

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            lines.add(String.format("%s: %s", entry.getKey() + " ".repeat(Math.max(7 - entry.getKey().length(), 0)), entry.getValue()));
        }

        String h2 = "Scope (Scoped symbol table) contents";
        lines.addAll(List.of(h2, "-".repeat(h2.length())));

        for (Map.Entry<String, Symbol> entry : symbols.entrySet()) {
            lines.add(String.format("%s: %s", entry.getKey() + " ".repeat(Math.max(7 - entry.getKey().length(), 0)), entry.getValue()));
        }

        lines.add("\n");

        return "\n" + String.join("\n", lines);
    }

    public void log(String msg) {
        if (Spi.SHOULD_LOG_SCOPE) {
            System.out.println(msg);
        }
    }

    public void insert(Symbol symbol) {
        log(String.format("Insert: %s", symbol.getName()));
        symbol.setScopeLevel(scopeLevel);
        symbols.put(symbol.getName(), symbol);
    }

    @Nullable
    public Symbol lookup(String name) {
        return lookup(name, false);
    }

    @Nullable
    public Symbol lookup(String name, boolean currentScopeOnly) {
        log(String.format("Lookup: %s (Scope name: %s)", name, scopeName));
        FuncSymbol funcSymbol = NativeCalls.get(name);
        if (funcSymbol != null) {
            return funcSymbol;
        }

        Symbol symbol = symbols.get(name);

        if (symbol != null) {
            return symbol;
        }

        if (currentScopeOnly) {
            return null;
        }

        if (enclosingScope != null) {
            return enclosingScope.lookup(name);
        }

        return null;
    }
}
