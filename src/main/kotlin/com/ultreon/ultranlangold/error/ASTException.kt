package com.ultreon.ultranlangold.error

import com.ultreon.ultranlangold.token.Token

open class ASTException(val errorCode: ErrorCode? = null, val token: Token? = null, message: String? = null) : RuntimeException(message)