package com.ultreon.ultranlangold.error

import com.ultreon.ultranlangold.token.Token

class LexerException(errorCode: ErrorCode? = null, token: Token? = null, message: String? = null) :
    ASTException(errorCode, token, message)