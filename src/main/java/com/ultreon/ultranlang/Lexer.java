package com.ultreon.ultranlang;

import com.ultreon.ultranlang.exception.LexerException;
import com.ultreon.ultranlang.token.Token;
import com.ultreon.ultranlang.token.TokenType;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.Objects;

public class Lexer {
    private final String text;

    private int prevPos = -1;
    private int pos = 0;
    private int lineno = 1;
    private int column = 1;

    public Lexer(String text) {
        this.text = text;
    }

    public void error() {
        String s = "Lexer error on '%s' at line %d, column %d".formatted(getCurrentChar(), lineno, column);
        throw new LexerException(s);
    }

    public void advance() {
        if (Objects.equals(getCurrentChar(), '\n')) {
            lineno++;
            column = 0;
        }

        pos++;
        if (pos >= text.length()) {
            // setCurrentChar(null);
        } else {
            // setCurrentChar(text.charAt(pos));
            column++;
        }
    }

    @Nullable
    public Character peek() {
        int peekPos = pos + 1;
        if (peekPos >= text.length()) {
            return null;
        } else {
            return text.charAt(peekPos);
        }
    }

    public void skipWhitespace() {
        while (getCurrentChar() != null && Character.isWhitespace(getCurrentChar())) {
            advance();
        }
    }

    public void skipComment() {
        while (!Objects.equals(getCurrentChar(), ']')) {
            advance();
        }
        advance();
    }

    public Token string() {
        StringBuilder s = new StringBuilder();
        while (!Objects.equals(getCurrentChar(), '"')) {
            Character currentChar = getCurrentChar();
            if (currentChar == null) {
                break;
            }
            if (currentChar == '\\') {
                advance();
                Character c = getCurrentChar();
                switch (c) {
                    case 'n' -> s.append('\n');
                    case 't' -> s.append('\t');
                    case 'r' -> s.append('\r');
                    case 'b' -> s.append('\b');
                    case '0' -> s.append('\0');
                    case 'x' -> {
                        advance();
                        advance();
                        String hex = text.substring(pos - 2, pos);
                        s.append((char) Integer.parseInt(hex, 16));
                    }
                    case 'u' -> {
                        advance();
                        advance();
                        advance();
                        advance();
                        String hex = text.substring(pos - 4, pos);
                        s.append((char) Integer.parseInt(hex, 16));
                    }
                    default -> s.append(c);
                }
            } else if (currentChar != '"') {
                s.append(currentChar);
            } else {
                break;
            }
            advance();
        }
        advance();
        return new Token(TokenType.STRING_CONST, s.toString(), lineno, column);
    }

    public Token number() {
        Token token = new Token(null, null, lineno, column);

        StringBuilder result = new StringBuilder();
        while (getCurrentChar() != null && Character.isDigit(getCurrentChar())) {
            result.append(getCurrentChar());
            advance();
        }

        if (getCurrentChar() == '.') {
            result.append(getCurrentChar());
            advance();
            while (getCurrentChar() != null && Character.isDigit(getCurrentChar())) {
                result.append(getCurrentChar());
                advance();
            }

            token.setType(TokenType.REAL_CONST);
            token.setValue(new BigDecimal(result.toString()));
        } else {
            token.setType(TokenType.INTEGER_CONST);
            token.setValue(new BigInteger(result.toString()));
        }

        return token;
    }

    Token id() {
        Token token = new Token(null, null, lineno, column);

        StringBuilder value = new StringBuilder();
        while (getCurrentChar() != null && Character.isLetterOrDigit(getCurrentChar())) {
            value.append(getCurrentChar());
            advance();
        }

        TokenType tokenType = Spi.RESERVED_KEYWORDS.get(value.toString().toUpperCase(Locale.ROOT));

        if (tokenType == null) {
            token.setType(TokenType.ID);
            token.setValue(value.toString());
        } else {
            token.setType(tokenType);
            token.setValue(value.toString().toUpperCase(Locale.ROOT));
        }

        return token;
    }

    public Token getNextToken() {
        prevPos = pos;

        while (getCurrentChar() != null) {
            if (Character.isWhitespace(getCurrentChar())) {
                skipWhitespace();
                continue;
            }

            if (getCurrentChar() == '[') {
                advance();
                skipComment();
                continue;
            }

            if (getCurrentChar() == '"') {
                advance();
                return string();
            }

            if (Character.isLetter(getCurrentChar())) {
                return id();
            }

            if (Character.isDigit(getCurrentChar())) {
                return number();
            }

            if (getCurrentChar() == '=') {
                Token token = new Token(
                        TokenType.ASSIGN,
                        TokenType.ASSIGN.value(), // ":="
                        lineno,
                        column
                );

                advance();
                return token;
            }

            TokenType tokenType;
            try {
                tokenType = new TokenType(getCurrentChar().toString());
            } catch (IllegalArgumentException e) {
                error();
                throw new IllegalStateException("Unreachable");
            }

            Token token = new Token(tokenType, tokenType.value(), lineno, column);
            advance();
            return token;
        }

        return new Token(TokenType.EOF, TokenType.EOF.value(), lineno, column);
    }

    @Nullable
    public Character getCurrentChar() {
        if (pos >= text.length()) {
            return null;
        }
        return text.charAt(pos);
    }

    public int getPrevPos() {
        return prevPos;
    }

    public void setPrevPos(int prevPos) {
        this.prevPos = prevPos;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getLineno() {
        return lineno;
    }

    public void setLineno(int lineno) {
        this.lineno = lineno;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
