package com.ultreon.ultranlang

import com.ultreon.ultranlang.ast.*
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
            println("Token: (${currentToken.type?.value}), expect: (${tokenType.value})")
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
        if (varNode is Var) {
            val progName = varNode.value as String
            eat(TokenType.SEMI)
            val nodes = statementList()
            val programNode = Program(progName)
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
        val node = Block(declarationNodes, compoundStatementNode)
        return node
    }


    /**
     * declarations : (VAR (variable_declaration SEMI)+)? procedure_declaration*
     */
    fun declarations(): List<AST> {
        val declarations = mutableListOf<AST>()
        if (currentToken.type == TokenType.VAR) {
            eat(TokenType.VAR)
            while (currentToken.type == TokenType.ID) {
                val varDeclaration = variableDeclarations()
                declarations.addAll(varDeclaration)
                eat(TokenType.SEMI)
            }
        }
        while (currentToken.type == TokenType.FUNCTION) {
            val procDeclaration = funcDeclaration()
            declarations.add(procDeclaration)
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
            paramNodes.add(Param(Var(paramToken), typeNode))
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
        val varNodes = mutableListOf(Var(currentToken))
        eat(TokenType.ID)

        while (currentToken.type == TokenType.COMMA) {
            eat(TokenType.COMMA)
            varNodes.add(Var(currentToken))
            eat(TokenType.ID)
        }

        eat(TokenType.COLON)

        val typeNode = typeSpec()
        val varDeclarations = mutableListOf<VarDecl>()
        for (varNode in varNodes) {
            varDeclarations.add(VarDecl(varNode, typeNode))
        }

        return varDeclarations
    }

    /**
     * variable_declaration : ID (COMMA ID)* COLON type_spec
     */
    fun variableDeclaration(): VarDecl {
        val varNode = Var(currentToken)
        eat(TokenType.ID)

        eat(TokenType.COLON)

        val typeNode = typeSpec()
        val varDeclaration = VarDecl(varNode, typeNode)

        return varDeclaration
    }

    /**
     * procedure_declaration :
     *   PROCEDURE ID (LPAREN formal_parameter_list RPAREN)? SEMI block SEMI
     */
    fun funcDeclaration(): FuncDeclaration {
        val hash = Any().hashCode().toUInt().toString(16)
        println("Starting function declaration ($hash) at ${currentToken.line}:${currentToken.column}")
        eat(TokenType.FUNCTION)
        val procName = currentToken.value as String
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

        println("Finished function declaration ($hash) at ${currentToken.line}:${currentToken.column}")

//        println("Function Declaration (current token): ${currentToken.type} (${lexer.lineno}, ${lexer.column}) ${lexer.pos}")

        val funcDeclaration = FuncDeclaration(procName, formalParams)

        for (node in nodes) {
            funcDeclaration.statements.add(node)
        }

        return funcDeclaration
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
    fun statementList(): List<AST> {
        val hash = Any().hashCode().toUInt().toString(16)
        println("Starting statement list ($hash) at ${currentToken.line}:${currentToken.column}")

        if (currentToken.line == 8 && currentToken.column == 5) {
            throw ParserException(ErrorCode.DEBUG, currentToken,
                "Started statement list at wrong place: ${currentToken.line}:${currentToken.column}")
        }

        val node = statement()

        val results = mutableListOf<AST>(node)

        while (currentToken.type == TokenType.SEMI) {
            eat(TokenType.SEMI)
//            println("statementList: currentToken = $currentToken")
            results.add(statement())
            println("Expect SEMI: currentToken = $currentToken")
        }

        println("Finished statement list ($hash) at ${currentToken.line}:${currentToken.column}")

        return results
    }

    /**
     * statement : compound_statement
     *           | proccall_statement
     *           | assignment_statement
     *           | empty
     */
    fun statement(): AST {
        val hash = Any().hashCode().toUInt().toString(16)
        println("Starting statement ($hash) at ${currentToken.line}:${currentToken.column}")
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
        } else {
            empty()
        }
        println("Finished statement ($hash) at ${currentToken.line}:${currentToken.column}")
        println("Statement: currentToken = $currentToken")
        return node
    }

    /**
     * proccall_statement : ID LPAREN (expr (COMMA expr)*)? RPAREN
     */
    fun funcCallStatement(): FuncCall {
        val token = currentToken

        val procName = currentToken.value as String

        eat(TokenType.ID)
        eat(TokenType.LPAREN)

        val actualParams = mutableListOf<AST>()
        if (currentToken.type != TokenType.RPAREN) {
            actualParams.add(expr())
        }

        while (currentToken.type == TokenType.COMMA) {
            eat(TokenType.COMMA)
            actualParams.add(expr())
        }

        eat(TokenType.RPAREN)

        return FuncCall(procName, actualParams, token)
    }

    /**
     * assignment_statement : variable ASSIGN expr
     */
    fun assignmentStatement(): Assign {
        val left = variable()
        if (left is Var) {
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
    fun variable(): AST {
        val node = Var(currentToken)
        val pos = lexer.prevPos
        eat(TokenType.ID)

        if (currentToken.type == TokenType.LPAREN) {
            lexer.pos = pos
            currentToken = getNextToken()
            return funcCallStatement()
        }
        return node
    }

    /**
     * An empty production
     */
    fun empty(): NoOp = NoOp()

    /**
     * expr : term ((PLUS | MINUS) term)*
     */
    fun expr(): AST {
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
    fun term(): AST {
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
    fun factor(): AST {
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
                eat(TokenType.INTEGER_CONST)
                return Num(token)
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
                return variable()
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
     *           | proccall_statement
     *           | assignment_statement
     *           | empty
     *
     * proccall_statement : ID LPAREN (expr (COMMA expr)*)? RPAREN
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