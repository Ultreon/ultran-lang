package com.ultreon.ultranlang

import com.ultreon.ultranlang.ast.*
import com.ultreon.ultranlang.classes.FileType
import com.ultreon.ultranlang.error.ErrorCode
import com.ultreon.ultranlang.error.ParserException
import com.ultreon.ultranlang.token.Token
import com.ultreon.ultranlang.token.TokenType
import com.ultreon.ultranlang.token.repr

class Parser(val lexer: Lexer) {
    private var depth: Int = 0

    // set current token to the first token taken from the input
    var currentToken: Token = getNextToken()
        private set

    fun getNextToken(): Token {
        return lexer.getNextToken()
    }

    fun error(errorCode: ErrorCode, token: Token) {
        throw ParserException(errorCode, token, "${errorCode.value} -> ${token.value} @ ${lexer.location}")
    }

    fun error(errorCode: ErrorCode, got: Token, expected: TokenType) {
        throw ParserException(errorCode, got,
            "${errorCode.value} at ${got.line}:${got.column} -> ${got.repr()} expected ${expected.repr()}")
    }

    fun eatEnd() {
        val type = currentToken.type
        if (type?.isNewline == true) {
            while(currentToken.type?.isNewline == true) {
                eat(type)
            }
        } else {
            error(ErrorCode.UNEXPECTED_STATEMENT_END, currentToken)
        }
    }

    fun eatSpace() {
        while(currentToken.type?.isSpace == true) {
            eat(currentToken.type!!)
        }
    }

    fun eat(tokenType: TokenType, customMessage: String? = null) {
        // compare the current token type with the passed token
        // type and if they match then "eat" the current token
        // and assign the next token to the self.current_token,
        // otherwise raise an exception.
        if (shouldLogTokens) {
            logger.debug("${currentToken.locationIndented} | Got token ${currentToken.repr()} -> expected ${tokenType.repr()}")
        }
        if (currentToken.type == tokenType) {
            if (tokenType == TokenType.LCURL) {
                depth++
            } else if (tokenType == TokenType.RCURL) {
                depth--
            }
            currentToken = getNextToken()
        } else {
            if (customMessage != null) {
                throw ParserException(null, currentToken, customMessage)
            }
            error(ErrorCode.UNEXPECTED_TOKEN, currentToken, tokenType)
        }
    }

    /**
     * program : PROGRAM variable SEMI block DOT
     */
    fun program(): Program {
        eat(TokenType.PROGRAM)
        val varNode = variable()
        if (varNode is VarRef) {
            val programName = varNode.value as String
            eatEnd()
            val nodes = statementList(endToken = TokenType.EOF)
            val programNode = Program(programName)
            programNode.statements += nodes
            eat(TokenType.EOF)
            return programNode
        } else {
            throw ParserException(ErrorCode.UNEXPECTED_TOKEN, currentToken)
        }
    }

    /**
     * block : declarations compound_statement
     */
    fun block(): Block {
        val declarationNodes = declarations()
        val compoundStatementNode = compoundStatement()
        return Block(declarationNodes, compoundStatementNode)
    }


    /**
     * declarations : (VAR (variable_declaration SEMI)+)? procedure_declaration*
     */
    fun declarations(): List<LangObj> {
        val declarations = mutableListOf<LangObj>()
        if (currentToken.type == TokenType.VAR) {
            eat(TokenType.VAR)
            while (currentToken.type == TokenType.ID) {
                val varDeclaration = variableDeclarations()
                declarations.addAll(varDeclaration)
                eatEnd()
            }
        }
        while (currentToken.type == TokenType.FUNCTION) {
            val funcDeclaration = funcDeclaration()
            declarations.add(funcDeclaration)
        }
        return declarations
    }

    /**
     * formal_parameters : ID (COMMA ID)* COLON type_spec
     */
    fun formalParameters(): List<Param> {
        val paramNodes = mutableListOf<Param>()

        val paramTokens = mutableListOf(currentToken)
        eat(TokenType.ID)
        while (currentToken.type == TokenType.COMMA) {
            eat(TokenType.COMMA)
            paramTokens.add(currentToken)
            eat(TokenType.ID)
        }

        eat(TokenType.COLON)
        val typeNode = typeSpec()

        for (paramToken in paramTokens) {
            paramNodes.add(Param(VarRef(paramToken), typeNode))
        }

        return paramNodes
    }

    /**
     * formal_parameter_list : formal_parameters
     *                       | formal_parameters SEMI formal_parameter_list
     */
    fun formalParameterList(): List<Param> {
        if (currentToken.type != TokenType.ID) {
            return emptyList()
        }

        val paramNodes = mutableListOf<Param>()
        paramNodes.addAll(formalParameters())
        while (currentToken.type?.isNewline == true) {
            eatEnd()
            paramNodes.addAll(formalParameters())
        }
        return paramNodes
    }

    /**
     * variable_declaration : ID (COMMA ID)* COLON type_spec
     */
    fun variableDeclarations(): List<VarDecl> {
        val varRefNodes = mutableListOf(VarRef(currentToken))
        eat(TokenType.ID)

        while (currentToken.type == TokenType.COMMA) {
            eat(TokenType.COMMA)
            varRefNodes.add(VarRef(currentToken))
            eat(TokenType.ID)
        }

        eat(TokenType.COLON)

        val typeNode = typeSpec()
        val varDeclarations = mutableListOf<VarDecl>()
        for (varNode in varRefNodes) {
            varDeclarations.add(VarDecl(varNode, typeNode))
        }

        return varDeclarations
    }

    /**
     * variable_declaration : ID (COMMA ID)* COLON type_spec
     */
    fun variableDeclaration(constant: Boolean = false): FieldDecl {
        val varRefNode = VarRef(currentToken)
        eat(TokenType.ID)

        eat(TokenType.COLON)

        val typeNode = typeSpec()

        return if (constant) ValDecl(varRefNode, typeNode) else VarDecl(varRefNode, typeNode)
    }

    /**
     * variable_declaration : ID (COMMA ID)* COLON type_spec
     */
    fun fieldDeclaration(fileType: FileType, classDeclaration: ClassDeclaration): FieldDecl {
        var static = false
        if (currentToken.type == TokenType.STATIC) {
            static = true
            eat(TokenType.STATIC)
        }

        val varRefNode = VarRef(currentToken)
        eat(TokenType.ID)

        eat(TokenType.COLON)

        val typeNode = typeSpec()

        return when (fileType) {
            FileType.VAR -> VarDecl(varRefNode, typeNode, classDeclaration, static)
            FileType.VAL -> ValDecl(varRefNode, typeNode, classDeclaration, static)
        }.also {
            (if (static) {
                classDeclaration.staticFields += it
                classDeclaration.staticMembers
            } else {
                classDeclaration.instanceFields += it
                classDeclaration.instanceMembers
            }) += it

        }
    }

    /**
     * procedure_declaration :
     *   PROCEDURE ID (LPAREN formal_parameter_list RPAREN)? SEMI block SEMI
     */
    fun funcDeclaration(): FuncDeclaration {
        val hash = Any().hashCode().toUInt().toString(16)
        logger.debug("${currentToken.locationIndented} | Starting function declaration ($hash) at ${currentToken.line}:${currentToken.column}")
        eat(TokenType.FUNCTION)
        val functionName = currentToken.value as String
        eat(TokenType.ID)
        eatSpace()
        val formalParams = mutableListOf<Param>()
        if (currentToken.type == TokenType.LPAREN) {
            eat(TokenType.LPAREN)
            eatSpace()
            formalParams.addAll(formalParameterList())
            eatSpace()
            eat(TokenType.RPAREN)
            eatSpace()
        }
        eat(TokenType.LCURL)
        eatSpace()
        val nodes = statementList(endToken = TokenType.RCURL)
        eat(TokenType.RCURL)

        logger.debug("${currentToken.locationIndented} | Finished function declaration ($hash) at ${currentToken.line}:${currentToken.column}")

        val funcDeclaration = FuncDeclaration(functionName, formalParams)

        for (node in nodes) {
            funcDeclaration.statements.add(node)
        }

        return funcDeclaration
    }

    /**
     * procedure_declaration :
     *   PROCEDURE ID (LPAREN formal_parameter_list RPAREN)? SEMI block SEMI
     */
    fun methodDeclaration(classDeclaration: ClassDeclaration): MethodDeclaration {
        val hash = Any().hashCode().toUInt().toString(16)
        logger.debug("${currentToken.locationIndented} | Starting method declaration ($hash) at ${currentToken.line}:${currentToken.column}")
        eat(TokenType.FUNCTION)
        val methodName = currentToken.value as String
        var static = false
        if (currentToken.type == TokenType.STATIC) {
            static = true
        }
        eat(TokenType.ID)
        val formalParams = mutableListOf<Param>()
        if (currentToken.type == TokenType.LPAREN) {
            eatSpace()
            eat(TokenType.LPAREN)
            eatSpace()
            formalParams.addAll(formalParameterList())
            eatSpace()
            eat(TokenType.RPAREN)
            eatSpace()
        }
        eatSpace()
        eat(TokenType.LCURL)
        eatSpace()
        val nodes = statementList(classDeclaration, TokenType.RCURL)
        eat(TokenType.RCURL)

        logger.debug("${currentToken.locationIndented} | Finished method declaration ($hash) at ${currentToken.line}:${currentToken.column}")

        val methodDeclaration = MethodDeclaration(methodName, static, formalParams, classDeclaration)

        for (node in nodes) {
            methodDeclaration.statements.add(node)
        }

        return methodDeclaration.also {
            (if (static) {
                classDeclaration.staticMethods += it
                classDeclaration.staticMembers
            } else {
                classDeclaration.instanceMethods += it
                classDeclaration.instanceMembers
            }) += it
        }
    }

    private fun constructorDeclaration(classDeclaration: ClassDeclaration): ConstructorDeclaration {
        val hash = Any().hashCode().toUInt().toString(16)
        logger.debug("${currentToken.locationIndented} | Starting constructor declaration ($hash) at ${currentToken.line}:${currentToken.column}")
        eat(TokenType.CONSTRUCTOR)
        eatSpace()
        val formalParams = mutableListOf<Param>()
        if (currentToken.type == TokenType.LPAREN) {
            eat(TokenType.LPAREN)
            eatSpace()
            formalParams.addAll(formalParameterList())
            eatSpace()
            eat(TokenType.RPAREN)
            eatSpace()
        }

        eat(TokenType.LCURL)
        eatSpace()

        val nodes = statementList(classDeclaration, TokenType.RCURL)
        eat(TokenType.RCURL)

        logger.debug("${currentToken.locationIndented} | Finished constructor declaration ($hash) at ${currentToken.line}:${currentToken.column}")

        val methodDeclaration = ConstructorDeclaration(formalParams, classDeclaration)

        for (node in nodes) {
            methodDeclaration.statements.add(node)
        }

        return methodDeclaration.also {
            classDeclaration.constructors += it
        }
    }

    /**
     * class_declaration : ID { statement_list }
     */
    fun classDeclaration(): ClassDeclaration {
        logger.debug("${currentToken.locationIndented} | Starting class")

        eat(TokenType.CLASS) // CLASS

        val className = currentToken.value as String
        val classDeclaration = ClassDeclaration(className)

        logger.debug("${currentToken.locationIndented} | Classname: $className")

        eat(TokenType.ID)    // ID
        eatSpace()
        eat(TokenType.LCURL) // {
        eatSpace()

        val nodes = classMemberList(classDeclaration, TokenType.RCURL)
        logger.debug("${currentToken.locationIndented} | Ending member list")
        eat(TokenType.RCURL, customMessage = "Expected class member end end, got ${currentToken.repr()} instead @ ${currentToken.locationIndented}")
        logger.debug("${currentToken.locationIndented} | Ending member list")

        for (node in nodes) {
            if (node is ClassInitDecl) classDeclaration.classInit.statements += node.statements
        }

        logger.debug("${currentToken.locationIndented} | Finishing class")

        return classDeclaration
    }

    /**
     * type_spec : INTEGER
     *           | STRING
     *           | REAL
     */
    fun typeSpec(): Type {
        val token = currentToken
        when (token.type) {
            TokenType.INTEGER -> eat(TokenType.INTEGER)
            TokenType.STRING -> eat(TokenType.STRING)
            else -> eat(TokenType.REAL)
        }
        return Type(token)
    }

    /**
     * compound_statement : BEGIN statement_list END
     */
    fun compoundStatement(): Compound {
        eat(TokenType.LCURL)
        val nodes = statementList(endToken = TokenType.RCURL)
        eat(TokenType.RCURL)

        val root = Compound()
        for (node in nodes) {
            root.children.add(node)
        }

        return root
    }

    /**
     * statement_list : statement
     *                | statement SEMI statement_list
     */
    fun statementList(classDeclaration: ClassDeclaration? = null, endToken: TokenType): List<LangObj> {
        val hash = Any().hashCode().toUInt().toString(16)
        logger.debug("${currentToken.locationIndented} | Starting statement list ($hash) at ${currentToken.line}:${currentToken.column}")

        if (currentToken.line == 8 && currentToken.column == 5) {
            throw ParserException(ErrorCode.DEBUG, currentToken,
                "Started statement list at wrong place: ${currentToken.line}:${currentToken.column}")
        }

        val node = statement(classDeclaration)

        val results = mutableListOf(node)

        while (currentToken.type != endToken) {
            eatEnd()
            if (currentToken.type == endToken) break
            results.add(statement(classDeclaration))
        }

        eatSpace()

        logger.debug("${currentToken.locationIndented} | Finished statement list ($hash) at ${currentToken.line}:${currentToken.column}")

        return results
    }

    fun classMemberList(classDeclaration: ClassDeclaration, endToken: TokenType): List<ClassMemberDecl> {
        val hash = Any().hashCode().toUInt().toString(16)
        logger.debug("${currentToken.locationIndented} | Starting class member list ($hash) at ${currentToken.line}:${currentToken.column}")

        if (currentToken.line == 8 && currentToken.column == 5) {
            throw ParserException(ErrorCode.DEBUG, currentToken,
                "Started statement list at wrong place: ${currentToken.line}:${currentToken.column}")
        }

        val node = classMember(classDeclaration)

        val results = mutableListOf(node)

        while (currentToken.type != endToken) {
            eatEnd()
            if (currentToken.type == endToken) break
            results.add(classMember(classDeclaration))
        }

        eatSpace()

        logger.debug("${currentToken.locationIndented} | Finished class member list ($hash) at ${currentToken.line}:${currentToken.column}")

        return results
    }

    /**
     * class_member : function (static) ID (
     *              |
     */
    private fun classMember(classDeclaration: ClassDeclaration): ClassMemberDecl {
        return when (currentToken.type) {
            TokenType.FUNCTION -> methodDeclaration(classDeclaration)
            TokenType.CONSTRUCTOR -> constructorDeclaration(classDeclaration)
            TokenType.VAL -> {
                eat(TokenType.VAL)
                fieldDeclaration(FileType.VAL, classDeclaration)
            }
            TokenType.VAR -> {
                eat(TokenType.VAR)
                fieldDeclaration(FileType.VAR, classDeclaration)
            }
            TokenType.LCURL -> classInit(classDeclaration, TokenType.RCURL)
            else -> throw ParserException(ErrorCode.UNEXPECTED_TOKEN, currentToken, "Token isn't valid for a class member: ${currentToken.repr()} (${currentToken.type?.value.repr()})")
        }
    }

    private fun classInit(classDeclaration: ClassDeclaration, endToken: TokenType): ClassInitDecl {
        eat(TokenType.LCURL)
        eatSpace()

        val hash = Any().hashCode().toUInt().toString(16)
        logger.debug("${currentToken.locationIndented} | Starting static init ($hash) at ${currentToken.line}:${currentToken.column}")

        if (currentToken.line == 8 && currentToken.column == 5) {
            throw ParserException(ErrorCode.DEBUG, currentToken,
                "Started statement list at wrong place: ${currentToken.line}:${currentToken.column}")
        }

        val node = statement()

        val statements = mutableListOf(node)

        while (currentToken.type != endToken) {
            eatEnd()
            if (currentToken.type == endToken) break
            statements.add(statement())
        }

        eat(TokenType.RCURL)

        logger.debug("${currentToken.locationIndented} | Finished static init ($hash) at ${currentToken.line}:${currentToken.column}")

        val classInit = ClassInitDecl()
        classInit.statements += statements
        return classInit.also {
            classDeclaration.classInit.statements += classInit.statements
        }
    }

    /**
     * statement : compound_statement
     *           | func_call_statement
     *           | assignment_statement
     *           | empty
     */
    fun statement(classDeclaration: ClassDeclaration? = null): LangObj {
        val hash = Any().hashCode().toUInt().toString(16)
        logger.debug("${currentToken.locationIndented} | Starting statement ($hash) at ${currentToken.line}:${currentToken.column}")
        logger.debug("${currentToken.locationIndented} | Current Token Type: ${currentToken.type?.value} @ ${lexer.lineno}:${lexer.column}")
        val node = when (currentToken.type) {
            TokenType.ID -> {
                if (lexer.currentChar == '(') funcCallStatement() else assignmentStatement()
            }
            TokenType.VAR -> {
                eat(TokenType.VAR)
                variableDeclaration() as LangObj
            }
            TokenType.VAL -> {
                eat(TokenType.VAL)
                variableDeclaration(constant = true) as LangObj
            }
            TokenType.FUNCTION -> {
                funcDeclaration()
            }
            TokenType.CLASS -> {
                classDeclaration()
            }
            else -> {
                if (classDeclaration != null) {
                    when (currentToken.type) {
                        TokenType.THIS -> {
                            eat(TokenType.THIS)
                            eat(TokenType.DOT)
                            if (lexer.currentChar == '(') funcCallStatement() else assignmentStatement()
                        }

                        TokenType.RCURL -> {
                            empty()
                        }

                        else -> {
                            error("Expected a statement or \"this\", but got ${currentToken.type?.value.repr()} @ ${lexer.lineno}:${lexer.column}")
                        }
                    }
                } else if (currentToken.type == TokenType.RCURL && depth > 0) {
                    empty()
                } else {
                    error("Expected a statement, but got ${currentToken.type?.value.repr()} @ ${lexer.lineno}:${lexer.column}")
                }
            }
        }
        logger.debug("${currentToken.locationIndented} | Finished statement ($hash) at ${currentToken.line}:${currentToken.column}")
        logger.debug("${currentToken.locationIndented} | Current statement token: ${currentToken.repr()}")
        return node
    }

    /**
     * func_call_statement : ID LPAREN (expr (COMMA expr)*)? RPAREN
     */
    fun funcCallStatement(): FuncCall {
        val token = currentToken

        val functionName = currentToken.value as String

        eat(TokenType.ID)
        eat(TokenType.LPAREN)

        val actualParams = mutableListOf<LangObj>()
        if (currentToken.type != TokenType.RPAREN) {
            actualParams.add(expr())
        }

        while (currentToken.type == TokenType.COMMA) {
            eat(TokenType.COMMA)
            actualParams.add(expr())
        }

        eat(TokenType.RPAREN)

        return FuncCall(functionName, actualParams, token)
    }

    /**
     * assignment_statement : variable ASSIGN expr
     */
    fun assignmentStatement(): Assign {
        val left = variable()
        if (left is VarRef) {
            val token = currentToken
            eat(TokenType.ASSIGN)
            val right = expr()
            return Assign(left, token, right)
        } else {
            throw ParserException(ErrorCode.UNEXPECTED_TOKEN, currentToken)
        }
    }

    /**
     * variable : ID
     */
    tailrec fun variable(parent: Returnable? = null): Returnable {
        var node: Returnable = VarRef(currentToken)
        val pos = lexer.prevPos
        parent?.let {
            it.child = node
        }
        eat(TokenType.ID)

        if (currentToken.type == TokenType.LPAREN) {
            lexer.pos = pos
            currentToken = getNextToken()
            node = funcCallStatement()
        }

        return if (currentToken.type != TokenType.DOT) {
            node
        } else {
            eat(TokenType.DOT)
            variable(node)
        }
    }

    /**
     * An empty production
     */
    fun empty(): NoOp = NoOp()

    /**
     * expr : term ((PLUS | MINUS) term)*
     */
    fun expr(): LangObj {
        var node = term()
        while (currentToken.type == TokenType.PLUS || currentToken.type == TokenType.MINUS) {
            val token = currentToken
            if (token.type == TokenType.PLUS) {
                eat(TokenType.PLUS)
            } else if (token.type == TokenType.MINUS) {
                eat(TokenType.MINUS)
            }
            node = BinOp(node, token, term())
        }
        return node
    }

    /**
     * term : factor ((MUL | INTEGER_DIV | FLOAT_DIV) factor)*
     */
    fun term(): LangObj {
        var node = factor()
        while (currentToken.type == TokenType.MUL || currentToken.type == TokenType.INTEGER_DIV || currentToken.type == TokenType.FLOAT_DIV) {
            val token = currentToken
            when (token.type) {
                TokenType.MUL -> {
                    eat(TokenType.MUL)
                }

                TokenType.INTEGER_DIV -> {
                    eat(TokenType.INTEGER_DIV)
                }

                TokenType.FLOAT_DIV -> {
                    eat(TokenType.FLOAT_DIV)
                }
            }
            node = BinOp(node, token, factor())
        }
        return node
    }

    /**
     * factor : PLUS factor
     *        | MINUS factor
     *        | INTEGER_CONST
     *        | REAL_CONST
     *        | LPAREN expr RPAREN
     *        | variable
     */
    fun factor(): LangObj {
        val token = currentToken
        when (token.type) {
            TokenType.PLUS -> {
                eat(TokenType.PLUS)
                return UnaryOp(token, factor())
            }

            TokenType.MINUS -> {
                eat(TokenType.MINUS)
                return UnaryOp(token, factor())
            }

            TokenType.INTEGER_CONST -> {
                eat(TokenType.INTEGER_CONST)
                return Num(token)
            }

            TokenType.STRING_CONST -> {
                eat(TokenType.STRING_CONST)
                return String(token)
            }

            TokenType.REAL_CONST -> {
                eat(TokenType.REAL_CONST)
                return Num(token)
            }

            TokenType.LPAREN -> {
                eat(TokenType.LPAREN)
                val node = expr()
                eat(TokenType.RPAREN)
                return node
            }

            else -> {
                return variable() as LangObj
            }
        }
    }

    /**
     *
     * program : PROGRAM variable SEMI block DOT
     *
     * block : declarations compound_statement
     *
     * declarations : (VAR (variable_declaration SEMI)+)? procedure_declaration*
     *
     * variable_declaration : ID (COMMA ID)* COLON type_spec
     *
     * procedure_declaration :
     *     PROCEDURE ID (LPAREN formal_parameter_list RPAREN)? SEMI block SEMI
     *
     * formal_params_list : formal_parameters
     *                    | formal_parameters SEMI formal_parameter_list
     *
     * formal_parameters : ID (COMMA ID)* COLON type_spec
     *
     * type_spec : INTEGER | REAL
     *
     * compound_statement : BEGIN statement_list END
     *
     * statement_list : statement
     *                | statement SEMI statement_list
     *
     * statement : compound_statement
     *           | func_call_statement
     *           | assignment_statement
     *           | empty
     *
     * func_call_statement : ID LPAREN (expr (COMMA expr)*)? RPAREN
     *
     * assignment_statement : variable ASSIGN expr
     *
     * empty :
     *
     * expr : term ((PLUS | MINUS) term)*
     *
     * term : factor ((MUL | INTEGER_DIV | FLOAT_DIV) factor)*
     *
     * factor : PLUS factor
     *        | MINUS factor
     *        | INTEGER_CONST
     *        | REAL_CONST
     *        | LPAREN expr RPAREN
     *        | variable
     *
     * variable: ID
     */
    fun parse(): Program {
        val node = program()
        if (currentToken.type != TokenType.EOF) {
            error(ErrorCode.UNEXPECTED_TOKEN, currentToken)
        }
        return node
    }
}