package com.ultreon.ultranlang

import com.ultreon.ultranlang.ast.*
import com.ultreon.ultranlang.classes.FileType
import com.ultreon.ultranlang.error.ErrorCode
import com.ultreon.ultranlang.error.ParserException
import com.ultreon.ultranlang.token.Token
import com.ultreon.ultranlang.token.TokenType

class Parser(val lexer: Lexer) {
    // set current token to the first token taken from the input
    var currentToken: Token = getNextToken()
        private set

    fun getNextToken(): Token {
        return lexer.getNextToken()
    }

    fun error(errorCode: ErrorCode, token: Token) {
        throw ParserException(errorCode, token, "${errorCode.value} -> ${token.value}")
    }

    fun error(errorCode: ErrorCode, got: Token, expected: TokenType) {
        throw ParserException(errorCode, got,
            "${errorCode.value} at ${got.line}:${got.column} -> ${got.type?.value} expected ${expected.value}")
    }

    fun eat(tokenType: TokenType) {
        // compare the current token type with the passed token
        // type and if they match then "eat" the current token
        // and assign the next token to the self.current_token,
        // otherwise raise an exception.
        if (shouldLogTokens) {
            logger.debug("Token: (${currentToken.type?.value}), expect: (${tokenType.value})")
        }
        if (currentToken.type == tokenType) {
            currentToken = getNextToken()
        } else {
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
            eat(TokenType.SEMI)
            val nodes = statementList()
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
                eat(TokenType.SEMI)
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
        while (currentToken.type == TokenType.SEMI) {
            eat(TokenType.SEMI)
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
    fun variableDeclaration(): VarDecl {
        val varRefNode = VarRef(currentToken)
        eat(TokenType.ID)

        eat(TokenType.COLON)

        val typeNode = typeSpec()

        return VarDecl(varRefNode, typeNode)
    }

    /**
     * variable_declaration : ID (COMMA ID)* COLON type_spec
     */
    fun fieldDeclaration(fileType: FileType): FieldDecl {
        val varRefNode = VarRef(currentToken)
        eat(TokenType.ID)

        eat(TokenType.COLON)

        val typeNode = typeSpec()

        return when (fileType) {
            FileType.VAR -> VarDecl(varRefNode, typeNode)
            FileType.VAL -> ValDecl(varRefNode, typeNode)
        }
    }

    /**
     * procedure_declaration :
     *   PROCEDURE ID (LPAREN formal_parameter_list RPAREN)? SEMI block SEMI
     */
    fun funcDeclaration(): FuncDeclaration {
        val hash = Any().hashCode().toUInt().toString(16)
        logger.debug("Starting function declaration ($hash) at ${currentToken.line}:${currentToken.column}")
        eat(TokenType.FUNCTION)
        val functionName = currentToken.value as String
        eat(TokenType.ID)
        val formalParams = mutableListOf<Param>()
        if (currentToken.type == TokenType.LPAREN) {
            eat(TokenType.LPAREN)
            formalParams.addAll(formalParameterList())
            eat(TokenType.RPAREN)
        }
        eat(TokenType.LCURL)
        val nodes = statementList()
        eat(TokenType.RCURL)

        logger.debug("Finished function declaration ($hash) at ${currentToken.line}:${currentToken.column}")

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
    fun methodDeclaration(): MethodDeclaration {
        val hash = Any().hashCode().toUInt().toString(16)
        logger.debug("Starting function declaration ($hash) at ${currentToken.line}:${currentToken.column}")
        eat(TokenType.FUNCTION)
        val methodName = currentToken.value as String
        var static = false
        if (currentToken.type == TokenType.STATIC) {
            static = true
        }
        eat(TokenType.ID)
        val formalParams = mutableListOf<Param>()
        if (currentToken.type == TokenType.LPAREN) {
            eat(TokenType.LPAREN)
            formalParams.addAll(formalParameterList())
            eat(TokenType.RPAREN)
        }
        eat(TokenType.LCURL)
        val nodes = statementList()
        eat(TokenType.RCURL)

        logger.debug("Finished function declaration ($hash) at ${currentToken.line}:${currentToken.column}")

        val methodDeclaration = MethodDeclaration(methodName, static, formalParams)

        for (node in nodes) {
            methodDeclaration.statements.add(node)
        }

        return methodDeclaration
    }

    /**
     * class_declaration - ID { statement_list }
     */
    fun classDeclaration(): ClassDeclaration {
        val className = currentToken.value as String
        val classDeclaration = ClassDeclaration(className)

        eat(TokenType.ID)    // ID
        eat(TokenType.LCURL) // {

        val nodes = classMemberList()

        eat(TokenType.RCURL) // }


        for (node in nodes) {
            when (node) {
                is FieldDecl -> classDeclaration.fields.add(node)
                is ConstructorDeclaration -> classDeclaration.constructors.add(node)
                is MethodDeclaration -> classDeclaration.methods.add(node)
                is ClassInitDecl -> classDeclaration.classInit.statements += node.statements
            }
        }

        return classDeclaration
    }

    /**
     * type_spec : INTEGER
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
        val nodes = statementList()
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
    fun statementList(): List<LangObj> {
        val hash = Any().hashCode().toUInt().toString(16)
        logger.debug("Starting statement list ($hash) at ${currentToken.line}:${currentToken.column}")

        if (currentToken.line == 8 && currentToken.column == 5) {
            throw ParserException(ErrorCode.DEBUG, currentToken,
                "Started statement list at wrong place: ${currentToken.line}:${currentToken.column}")
        }

        val node = statement()

        val results = mutableListOf(node)

        while (currentToken.type == TokenType.SEMI) {
            eat(TokenType.SEMI)
            results.add(statement())
            logger.debug("Expect SEMI: currentToken = $currentToken")
        }

        logger.debug("Finished statement list ($hash) at ${currentToken.line}:${currentToken.column}")

        return results
    }

    fun classMemberList(): List<ClassMemberDecl> {
        val hash = Any().hashCode().toUInt().toString(16)
        logger.debug("Starting statement list ($hash) at ${currentToken.line}:${currentToken.column}")

        if (currentToken.line == 8 && currentToken.column == 5) {
            throw ParserException(ErrorCode.DEBUG, currentToken,
                "Started statement list at wrong place: ${currentToken.line}:${currentToken.column}")
        }

        val node = classMember()

        val results = mutableListOf(node)

        while (currentToken.type == TokenType.SEMI) {
            eat(TokenType.SEMI)
            results.add(classMember())
            logger.debug("Expect SEMI: currentToken = $currentToken")
        }

        logger.debug("Finished statement list ($hash) at ${currentToken.line}:${currentToken.column}")

        return results
    }

    /**
     * class_member : function (static) ID (
     *              |
     */
    private fun classMember(): ClassMemberDecl {
        return when (currentToken.type) {
            TokenType.FUNCTION -> methodDeclaration()
            TokenType.CONSTRUCTOR -> constructorDeclaration()
            TokenType.VAL -> fieldDeclaration(FileType.VAL)
            TokenType.VAR -> fieldDeclaration(FileType.VAR)
            TokenType.LCURL -> staticInit()
            else -> throw ParserException(ErrorCode.UNEXPECTED_TOKEN, currentToken, "Token isn't valid for a class member: ${currentToken.value} (${currentToken.type?.value})")
        }
    }

    private fun staticInit(): ClassInitDecl {
        val hash = Any().hashCode().toUInt().toString(16)
        logger.debug("Starting statement list ($hash) at ${currentToken.line}:${currentToken.column}")

        if (currentToken.line == 8 && currentToken.column == 5) {
            throw ParserException(ErrorCode.DEBUG, currentToken,
                "Started statement list at wrong place: ${currentToken.line}:${currentToken.column}")
        }

        val node = statement()

        val statements = mutableListOf(node)

        while (currentToken.type == TokenType.SEMI) {
            eat(TokenType.SEMI)
            statements.add(statement())
            logger.debug("Expect SEMI: currentToken = $currentToken")
        }

        logger.debug("Finished statement list ($hash) at ${currentToken.line}:${currentToken.column}")

        val classInit = ClassInitDecl()
        classInit.statements += statements
        return classInit
    }

    private fun constructorDeclaration(): ConstructorDeclaration {
        val hash = Any().hashCode().toUInt().toString(16)
        logger.debug("Starting function declaration ($hash) at ${currentToken.line}:${currentToken.column}")
        eat(TokenType.CONSTRUCTOR)
        val formalParams = mutableListOf<Param>()
        if (currentToken.type == TokenType.LPAREN) {
            eat(TokenType.LPAREN)
            formalParams.addAll(formalParameterList())
            eat(TokenType.RPAREN)
        }
        eat(TokenType.LCURL)
        val nodes = statementList()
        eat(TokenType.RCURL)

        logger.debug("Finished function declaration ($hash) at ${currentToken.line}:${currentToken.column}")

        val methodDeclaration = ConstructorDeclaration(formalParams)

        for (node in nodes) {
            methodDeclaration.statements.add(node)
        }

        return methodDeclaration
    }

    /**
     * statement : compound_statement
     *           | func_call_statement
     *           | assignment_statement
     *           | empty
     */
    fun statement(): LangObj {
        val hash = Any().hashCode().toUInt().toString(16)
        logger.debug("Starting statement ($hash) at ${currentToken.line}:${currentToken.column}")
        val node = /*if (currentToken.type == TokenType.BEGIN) {
            compoundStatement()
        } else */if (currentToken.type == TokenType.ID && lexer.currentChar == '(') {
            funcCallStatement()
        } else if (currentToken.type == TokenType.ID) {
            assignmentStatement()
        } else if (currentToken.type == TokenType.VAR) {
            eat(TokenType.VAR)
            variableDeclaration()
        } else if (currentToken.type == TokenType.FUNCTION) {
//            eat(TokenType.VAR)
            funcDeclaration()
        } else if (currentToken.type == TokenType.CLASS) {
//            eat(TokenType.VAR)
            classDeclaration()
        } else {
            empty()
        }
        logger.debug("Finished statement ($hash) at ${currentToken.line}:${currentToken.column}")
        logger.debug("Statement: currentToken = $currentToken")
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