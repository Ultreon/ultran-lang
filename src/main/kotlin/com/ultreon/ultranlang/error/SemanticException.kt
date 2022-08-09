package com.ultreon.ultranlang.error

import com.ultreon.ultranlang.token.Token

class SemanticException(errorCode: ErrorCode? = null, token: Token? = null, message: String? = null) : ASTException(errorCode,  token, message) {

}