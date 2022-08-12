package com.ultreon.ultranlang;

import com.ultreon.ultranlang.token.TokenType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Spi {
    static Map<String, TokenType> buildReservedKeywords() {
        List<TokenType> ttList = TokenType.getTypes();
        int startIndex = ttList.indexOf(TokenType.PROGRAM);
        int endIndex = ttList.indexOf(TokenType.RETURN);
        HashMap<String, TokenType> reservedKeywords = new HashMap<>();

        for (int i = startIndex; i <= endIndex; i++) {
            reservedKeywords.put(ttList.get(i).value(), ttList.get(i));
        }

        return Collections.unmodifiableMap(reservedKeywords);
    }

    public static final Map<String, TokenType> RESERVED_KEYWORDS = buildReservedKeywords();

    public static boolean SHOULD_LOG_SCOPE = false;
    public static boolean SHOULD_LOG_STACK = false;
    public static boolean SHOULD_LOG_TOKENS = false;
    public static boolean SHOULD_LOG_INTERNAL_ERRORS = false;
}
