package com.ultreon.ultranlang;

import com.ultreon.ultranlang.ast.*;
import com.ultreon.ultranlang.exception.ErrorCode;
import com.ultreon.ultranlang.exception.ParserException;
import com.ultreon.ultranlang.token.Token;
import com.ultreon.ultranlang.token.TokenType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Parser {
    private final Lexer lexer;
    private Token currentToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.getNextToken();
    }

    private Token getNextToken() {
        return lexer.getNextToken();
    }

    public Lexer getLexer() {
        return lexer;
    }

    public Token getCurrentToken() {
        return currentToken;
    }

    public void error(ErrorCode errorCode, Token token) {
        throw new ParserException(errorCode, token, "%s -> %s".formatted(errorCode.getValue(), token.getValue()));
    }

    public void error(ErrorCode errorCode, Token got, TokenType expected) {
        throw new ParserException(errorCode, got, "%s at %d:%d -> Got %s expected %s".formatted(errorCode.getValue(), got.getLine(), got.getColumn(), got.getValue(), expected.value()));
    }

    public void eat(TokenType tokenType) {
        // compare the current token type with the passed token
        // type and if they match then "eat" the current token
        // and assign the next token to the self.current_token,
        // otherwise raise an exception.
        if (Spi.SHOULD_LOG_TOKENS) {
            System.out.printf("Token: (%s), expect: (%s)%n", currentToken.getType().value(), tokenType.value());
        }
        if (currentToken.getType().equals(tokenType)) {
            currentToken = getNextToken();
        } else {
            error(ErrorCode.UNEXPECTED_TOKEN, currentToken, tokenType);
        }
    }

    public Program program() {
        eat(TokenType.PROGRAM);
        AST node = variable();
        if (node instanceof Var varNode) {
            String progName = (String) varNode.getValue();
            eat(TokenType.SEMI);
            List<AST> nodes = statementList();
            Program programNode = new Program(progName);
            programNode.setStatements(nodes);
            eat(TokenType.EOF);
            return programNode;
        } else {
            throw new ParserException(ErrorCode.UNEXPECTED_TOKEN, currentToken, "Expected to find a string literal");
        }
    }

    public Block block() {
        List<AST> declarationNodes = declarations();
        Compound compoundNode = compoundStatement();
        return new Block(declarationNodes, compoundNode);
    }

    public List<AST> declarations() {
        List<AST> declarationNodes = new ArrayList<>();
        if (currentToken.getType().equals(TokenType.VAR)) {
            eat(TokenType.VAR);
            while (currentToken.getType().equals(TokenType.ID)) {
                List<VarDecl> varDeclarations = variableDeclarations();
                declarationNodes.addAll(varDeclarations);
                eat(TokenType.SEMI);
            }
        }
        while (currentToken.getType().equals(TokenType.FUNCTION)) {
            FuncDeclaration procDeclaration = funcDeclaration();
            declarationNodes.add(procDeclaration);
        }
        return declarationNodes;
    }

    public List<Param> formalParameters() {
        List<Param> paramNodes = new ArrayList<>();

        List<Token> paramTokens = new ArrayList<>();
        paramTokens.add(currentToken);

        eat(TokenType.ID);

        while (currentToken.getType().equals(TokenType.COMMA)) {
            eat(TokenType.COMMA);
            paramTokens.add(currentToken);
            eat(TokenType.ID);
        }

        eat(TokenType.COLON);
        Type typeNode = typeSpec();

        for (Token paramToken : paramTokens) {
            paramNodes.add(new Param(new Var(paramToken), typeNode));
        }

        return paramNodes;
    }

    public List<Param> formalParamaterList() {
        if (!currentToken.getType().equals(TokenType.ID)) {
            return Collections.emptyList();
        }

        List<Param> paramNodes = new ArrayList<>(formalParameters());

        while (currentToken.getType().equals(TokenType.SEMI)) {
            eat(TokenType.SEMI);
            paramNodes.addAll(formalParameters());
        }

        return paramNodes;
    }

    public List<VarDecl> variableDeclarations() {
        List<Var> varNodes = new ArrayList<>();
        varNodes.add(new Var(currentToken));

        while (currentToken.getType().equals(TokenType.COMMA)) {
            eat(TokenType.COMMA);
            varNodes.add(new Var(currentToken));
            eat(TokenType.ID);
        }

        eat(TokenType.COLON);

        Type typeNode = typeSpec();
        List<VarDecl> varDeclarations = new ArrayList<>();
        for (Var varNode : varNodes) {
            varDeclarations.add(new VarDecl(varNode, typeNode));
        }

        return varDeclarations;
    }

    public VarDecl variableDeclaration() {
        Var varNode = new Var(currentToken);
        eat(TokenType.ID);

        eat(TokenType.COLON);

        Type typeNode = typeSpec();
        return new VarDecl(varNode, typeNode);
    }

    public FuncDeclaration funcDeclaration() {
        String hash = Integer.toUnsignedString(new Object().hashCode(), 16);
        eat(TokenType.FUNCTION);
        String funcName = (String) currentToken.getValue();
        eat(TokenType.ID);
        List<Param> formalParams = new ArrayList<>();
        if (currentToken.getType().equals(TokenType.LPAREN)) {
            eat(TokenType.LPAREN);
            formalParams.addAll(formalParamaterList());
            eat(TokenType.RPAREN);
        }

        eat(TokenType.LCURL);
        List<AST> nodes = statementList();
        eat(TokenType.RCURL);

        FuncDeclaration funcDeclaration = new FuncDeclaration(funcName, formalParams);

        for (AST node : nodes) {
            funcDeclaration.addStatement(node);
        }

        return funcDeclaration;
    }

    public Type typeSpec() {
        Token token = currentToken;
        if (token.getType().equals(TokenType.INTEGER)) {
            eat(TokenType.INTEGER);
        } else if (token.getType().equals(TokenType.STRING)) {
            eat(TokenType.STRING);
        } else if (token.getType().equals(TokenType.BOOLEAN)) {
            eat(TokenType.BOOLEAN);
        } else if (token.getType().equals(TokenType.REAL)) {
            eat(TokenType.REAL);
        } else {
            throw new ParserException(ErrorCode.UNEXPECTED_TOKEN, token, "Expected to find a type specifier");
        }

        return new Type(token);
    }

    public Compound compoundStatement() {
        eat(TokenType.LCURL);
        List<AST> nodes = statementList();
        eat(TokenType.RCURL);

        Compound root = new Compound();
        for (AST node : nodes) {
            root.addChild(node);
        }

        return root;
    }

    public List<AST> statementList() {
        String hash = Integer.toUnsignedString(new Object().hashCode(), 16);

        AST node = statement();

        List<AST> results = new ArrayList<>();
        results.add(node);

        while (currentToken.getType().equals(TokenType.SEMI)) {
            eat(TokenType.SEMI);
            results.add(statement());
        }

        return results;
    }

    public AST statement() {
        String hash = Integer.toUnsignedString(new Object().hashCode(), 16);

        AST node;
        if (currentToken.getType().equals(TokenType.ID) && Objects.equals(lexer.getCurrentChar(), '(')) {
            node = funcCallStatement();
        } else if (currentToken.getType().equals(TokenType.ID)) {
            node = assignmentStatement();
        } else if (currentToken.getType().equals(TokenType.VAR)) {
            eat(TokenType.VAR);
            node = variableDeclaration();
        } else if (currentToken.getType().equals(TokenType.FUNCTION)) {
            node = funcDeclaration();
        } else {
            node = empty();
        }

        return node;
    }

    public FuncCall funcCallStatement() {
        Token token = currentToken;

        String funcName = (String) token.getValue();

        eat(TokenType.ID);
        eat(TokenType.LPAREN);

        List<AST> actualParams = new ArrayList<>();
        if (!currentToken.getType().equals(TokenType.RPAREN)) {
            actualParams.add(expr());
        }

        while (currentToken.getType().equals(TokenType.COMMA)) {
            eat(TokenType.COMMA);
            actualParams.add(expr());
        }

        eat(TokenType.RPAREN);

        return new FuncCall(funcName, actualParams, token);
    }

    public Assign assignmentStatement() {
        AST left = variable();
        if (left instanceof Var varNode) {
            Token token = currentToken;
            eat(TokenType.ASSIGN);
            AST right = expr();
            return new Assign(varNode, token, right);
        } else {
            throw new ParserException(ErrorCode.UNEXPECTED_TOKEN, currentToken, "Expected to find a variable");
        }
    }

    public AST variable() {
        Var node = new Var(currentToken);
        int pos = lexer.getPrevPos();
        eat(TokenType.ID);

        if (currentToken.getType().equals(TokenType.LPAREN)) {
            lexer.setPos(pos);
            currentToken = getNextToken();
            return funcCallStatement();
        }

        return node;
    }

    public AST empty() {
        return new NoOp();
    }

    public AST expr() {
        AST node = term();
        while (currentToken.getType().equals(TokenType.PLUS) || currentToken.getType().equals(TokenType.MINUS)) {
            Token token = currentToken;
            if (token.getType().equals(TokenType.PLUS)) {
                eat(TokenType.PLUS);
            } else {
                eat(TokenType.MINUS);
            }
            node = new BinOp(node, token, term());
        }
        return node;
    }

    public AST term() {
        AST node = factor();
        while (currentToken.getType().equals(TokenType.MUL) || currentToken.getType().equals(TokenType.INTEGER_DIV) || currentToken.getType().equals(TokenType.FLOAT_DIV)) {
            Token token = currentToken;
            if (token.getType().equals(TokenType.MUL)) {
                eat(TokenType.MUL);
            } else if (token.getType().equals(TokenType.INTEGER_DIV)) {
                eat(TokenType.INTEGER_DIV);
            } else if (token.getType().equals(TokenType.FLOAT_DIV)) {
                eat(TokenType.FLOAT_DIV);
            } else {
                throw new ParserException(ErrorCode.UNEXPECTED_TOKEN, currentToken, "Expected to find a term");
            }
            node = new BinOp(node, token, factor());
        }
        return node;
    }

    public AST factor() {
        Token token = currentToken;
        if (token.getType().equals(TokenType.PLUS)) {
            eat(TokenType.PLUS);
            return new UnaryOp(token, factor());
        } else if (token.getType().equals(TokenType.MINUS)) {
            eat(TokenType.MINUS);
            return new UnaryOp(token, factor());
        } else if (token.getType().equals(TokenType.INTEGER_CONST)) {
            eat(TokenType.INTEGER_CONST);
            return new Num(token);
        } else if (token.getType().equals(TokenType.REAL_CONST)) {
            eat(TokenType.REAL_CONST);
            return new Num(token);
        } else if (token.getType().equals(TokenType.STRING_CONST)) {
            eat(TokenType.STRING_CONST);
            return new Str(token);
        } else if (token.getType().equals(TokenType.TRUE)) {
            eat(TokenType.TRUE);
            return new Bool(token);
        } else if (token.getType().equals(TokenType.FALSE)) {
            eat(TokenType.FALSE);
            return new Bool(token);
        } else if (token.getType().equals(TokenType.LPAREN)) {
            eat(TokenType.LPAREN);
            AST node = expr();
            eat(TokenType.RPAREN);
            return node;
        } else if (token.getType().equals(TokenType.ID)) {
            return variable();
        } else {
            throw new ParserException(ErrorCode.UNEXPECTED_TOKEN, currentToken, "Unexpected token at " + token.getLine() + ":" + token.getColumn() + " -> Expected to find a factor. got " + token.getType());
        }
    }

    public Program parse() {
        Program program = program();
        if (!currentToken.getType().equals(TokenType.EOF)) {
            throw new ParserException(ErrorCode.UNEXPECTED_TOKEN, currentToken, "Expected to find EOF");
        }
        return program;
    }
}
