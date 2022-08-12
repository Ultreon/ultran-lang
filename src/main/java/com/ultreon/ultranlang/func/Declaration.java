package com.ultreon.ultranlang.func;

import com.ultreon.ultranlang.ActivationRecord;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface Declaration {
    @Nullable
    Object invoke(ActivationRecord ar);
}
