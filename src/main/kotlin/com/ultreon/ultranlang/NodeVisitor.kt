package com.ultreon.ultranlang

import com.ultreon.ultranlang.annotations.Visit
import com.ultreon.ultranlang.ast.AST
import java.lang.IllegalStateException

open class NodeVisitor {
    fun visit(node: AST): Any? {
        for (member in this::class.members) {
            for (annotation in member.annotations) {
                if (annotation is Visit) {
                    if (annotation.value.isInstance(node)) {
                        return member.call(this, node)
                    }
                }
            }
        }
        throw IllegalStateException("No visitor found for ${node::class.qualifiedName}")
    }
}