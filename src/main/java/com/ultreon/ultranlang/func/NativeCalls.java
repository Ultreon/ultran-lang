package com.ultreon.ultranlang.func;

import com.ultreon.ultranlang.ActivationRecord;
import com.ultreon.ultranlang.exception.ScriptException;
import com.ultreon.ultranlang.exception.SemanticException;
import com.ultreon.ultranlang.symbol.BuiltinTypeSymbol;
import com.ultreon.ultranlang.symbol.FuncSymbol;
import com.ultreon.ultranlang.symbol.VarSymbol;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NativeCalls {
    private static final Map<String, FuncSymbol> symbols = new HashMap<>();
    private static final Map<String, Declaration> declarations = new HashMap<>();

    @Nullable
    public static Object nativeCall(FuncSymbol func, List<VarSymbol> args, ActivationRecord ar) {
        final String funcName = func.getName();
        if (declarations.containsKey(funcName)) {
            return declarations.get(funcName).invoke(ar);
        } else {
            throw new SemanticException("Native function " + funcName + " is not defined");
        }
    }

    public static boolean exists(String name) {
        return symbols.containsKey(name);
    }

    public static void load() {
        register("print", ParamBuilder.create().add("message", BuiltinTypeSymbol.STRING), ar -> {
            Object messageObj = ar.get("message");
            if (messageObj instanceof String message) {
                System.out.println(message);
            } else {
                System.out.println(messageObj);
            }
            return null;
        });
        register("randInt", ParamBuilder.create().add("x", BuiltinTypeSymbol.INTEGER).add("y", BuiltinTypeSymbol.INTEGER), ar -> {
            Object x = ar.get("x");
            Object y = ar.get("y");

            if (x instanceof BigInteger xInt && y instanceof BigInteger yInt) {
                return new Random().nextInt(xInt.intValue(), yInt.intValue());
            } else {
                throw new ScriptException("randInt expects two integers");
            }
        });
    }

    private static void register(String print, ParamBuilder builder, Declaration declaration) {
        symbols.put(print, new FuncSymbol(print, builder.build()
                .entrySet()
                .stream()
                .map(e -> new VarSymbol(e.getKey(), new BuiltinTypeSymbol(e.getValue())))
                .toList()));
        declarations.put(print, declaration);
    }

    @Nullable
    public static FuncSymbol get(String name) {
        return symbols.get(name);
    }
}
