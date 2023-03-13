package com.ultreon.ultranlang

import com.soywiz.kds.Stack
import com.ultreon.ultranlang.classes.ULObject

typealias CallStack = Stack<ActivationRecord>
typealias ObjectStack = Stack<ULObject>
