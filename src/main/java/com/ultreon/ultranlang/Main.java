package com.ultreon.ultranlang;

import com.ultreon.ultranlang.ast.Program;
import com.ultreon.ultranlang.exception.LexerException;
import com.ultreon.ultranlang.exception.ParserException;
import com.ultreon.ultranlang.exception.SemanticException;
import com.ultreon.ultranlang.func.NativeCalls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(@NotNull String[] args) {
        List<String> flags = new ArrayList<>();
        List<String> arguments = new ArrayList<>();

        for (String arg : args) {
            if (arg.startsWith("--")) {
                flags.add(arg.substring(2));
            } else {
                arguments.add(arg);
            }
        }

        Spi.SHOULD_LOG_SCOPE = flags.remove("scope");
        Spi.SHOULD_LOG_STACK = flags.remove("stack");
        Spi.SHOULD_LOG_TOKENS = flags.remove("tokens");
        Spi.SHOULD_LOG_INTERNAL_ERRORS = flags.remove("internal-errors");

        if (!flags.isEmpty()) {
            System.err.println("Unknown flags: " + flags);
            System.exit(1);
        }

        if (arguments.isEmpty()) {
            System.err.println("No input file specified");
            System.exit(1);
        }

        if (arguments.size() > 1) {
            System.err.println("Only one input file allowed");
            System.exit(1);
        }

        File inputFile = new File(arguments.get(0));

        if (!inputFile.exists()) {
            System.err.println("Input file does not exist");
            System.exit(1);
        }

        String text = null;
        try {
            text = Files.readString(inputFile.toPath());
        } catch (IOException e) {
            System.err.println("Failed to read input file");
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        }

        NativeCalls.load();

        Lexer lexer = new Lexer(text);
        Program tree;

        try {
            Parser parser = new Parser(lexer);
            tree = parser.parse();
        } catch (LexerException | ParserException e) {
            if (Spi.SHOULD_LOG_INTERNAL_ERRORS) e.printStackTrace();
            System.err.println(e.getMessage());
            System.exit(1);
            return;
        }

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

        try {
            semanticAnalyzer.visit(tree);
        } catch (SemanticException e) {
            if (Spi.SHOULD_LOG_INTERNAL_ERRORS) e.printStackTrace();
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Interpreter interpreter = new Interpreter(tree);
        interpreter.interpret();
    }
}
