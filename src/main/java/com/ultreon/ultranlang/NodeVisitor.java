package com.ultreon.ultranlang;

import com.ultreon.ultranlang.annotations.Visit;
import com.ultreon.ultranlang.ast.AST;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class NodeVisitor {
    @Nullable
    public Object visit(AST node) {
        for (Method method : this.getClass().getMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                if (annotation.annotationType().equals(Visit.class)) {
                    if (method.getParameterTypes()[0].isAssignableFrom(node.getClass())) {
                        try {
                            return method.invoke(this, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                    } else {
                    }
                } else {
                }
            }
        }

        throw new IllegalArgumentException("No visitor found for " + node.getClass().getName());
    }
}
