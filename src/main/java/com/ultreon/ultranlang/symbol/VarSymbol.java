package com.ultreon.ultranlang.symbol;

import com.ultreon.ultranlang.Repr;
import org.jetbrains.annotations.Nullable;

public class VarSymbol extends Symbol implements Repr {
    public VarSymbol(String name, @Nullable Symbol type) {
        super(name, type);
    }
}
