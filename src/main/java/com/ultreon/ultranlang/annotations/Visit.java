package com.ultreon.ultranlang.annotations;

import com.ultreon.ultranlang.ast.AST;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Visit {
    Class<? extends AST> value();
}
