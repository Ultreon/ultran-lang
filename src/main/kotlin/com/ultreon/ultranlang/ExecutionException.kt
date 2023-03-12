package com.ultreon.ultranlang

import com.ultreon.ultranlang.classes.ULObject

class ExecutionException(val vmInternalException: ULObject) : RuntimeException(vmInternalException.methods[]) {
}
