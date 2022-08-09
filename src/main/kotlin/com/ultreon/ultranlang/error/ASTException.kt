package com.ultreon.ultranlang.error

import com.ultreon.ultranlang.token.Token

open class ASTException(val errorCode: ErrorCode? = null, val token: Token? = null, message: String? = null) : RuntimeException(message)