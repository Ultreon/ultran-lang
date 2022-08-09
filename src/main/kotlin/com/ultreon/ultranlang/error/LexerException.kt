package com.ultreon.ultranlang.error

import com.ultreon.ultranlang.token.Token

class LexerException(errorCode: ErrorCode? = null, token: Token? = null, message: String? = null) : ASTException(errorCode,  token, message) {

}