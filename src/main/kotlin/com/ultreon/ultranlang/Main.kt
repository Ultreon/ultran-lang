package com.ultreon.ultranlang

import com.ultreon.ultranlang.ast.Program
import com.ultreon.ultranlang.error.LexerException
import com.ultreon.ultranlang.error.ParserException
import com.ultreon.ultranlang.error.SemanticException
import java.io.File
import java.lang.reflect.InvocationTargetException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val flags = mutableListOf<String>()
    val arguments = mutableListOf<String>()

    for (arg in args) {
        if (arg.startsWith("--")) {
            flags.add(arg.substring(2))
        } else {
            arguments.add(arg)
        }
    }

    SHOULD_LOG_SCOPE = flags.remove("scope")
    SHOULD_LOG_STACK = flags.remove("stack")

    if (flags.isNotEmpty()) {
        println("Unknown flags: ${flags.joinToString(", ")}")
        return
    }

    if (arguments.isEmpty()) {
        println("No arguments provided")
        return
    }

    val inputFile = File(arguments[0])

    if (!inputFile.exists()) {
        println("File not found: ${inputFile.absolutePath}")
        return
    }

    val text = inputFile.readText()

    val lexer = Lexer(text)
    val tree: Program
    try {
        val parser = Parser(lexer)
        tree = parser.parse()
    } catch (e: LexerException) {
        e.printStackTrace()
        println(e.message)
        exitProcess(1)
    } catch (e: ParserException) {
        e.printStackTrace()
        println(e.message)
        exitProcess(1)
    } catch (e: InvocationTargetException) {
        var cause = e.cause
        while (cause is InvocationTargetException) {
            cause = cause.cause
        }
        if (cause is LexerException) {
            cause.printStackTrace()
            println(cause.message)
            exitProcess(1)
        } else if (cause is ParserException) {
            cause.printStackTrace()
            println(cause.message)
            exitProcess(1)
        } else {
            throw e
        }
    }

    val semanticAnalyzer = SemanticAnalyzer()

    try {
        semanticAnalyzer.visit(tree)
    } catch (e: SemanticException) {
        e.printStackTrace()
        println(e.message)
        exitProcess(1)
    } catch (e: InvocationTargetException) {
        var cause = e.cause
        while (cause is InvocationTargetException) {
            cause = cause.cause
        }
        if (cause is SemanticException) {
            cause.printStackTrace()
            println(cause.message)
            exitProcess(1)
        } else {
            throw e
        }
    }

    val interpreter = Interpreter(tree)
    interpreter.interpret()
}
