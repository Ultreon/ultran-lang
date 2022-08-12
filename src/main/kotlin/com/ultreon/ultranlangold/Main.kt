package com.ultreon.ultranlangold

import com.ultreon.ultranlangold.ast.Program
import com.ultreon.ultranlangold.error.LexerException
import com.ultreon.ultranlangold.error.ParserException
import com.ultreon.ultranlangold.error.SemanticException
import com.ultreon.ultranlangold.func.NativeCalls
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

    shouldLogScope = flags.remove("scope")
    shouldLogStack = flags.remove("stack")
    shouldLogTokens = flags.remove("tokens")
    shouldLogInternalErrors = flags.remove("internal-errors")

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

    NativeCalls.load()

    val lexer = Lexer(text)
    val tree: Program
    try {
        val parser = Parser(lexer)
        tree = parser.parse()
    } catch (e: LexerException) {
        if (shouldLogInternalErrors) e.printStackTrace()
        printerr(e.message)
        exitProcess(1)
    } catch (e: ParserException) {
        if (shouldLogInternalErrors) e.printStackTrace()
        printerr(e.message)
        exitProcess(1)
    } catch (e: InvocationTargetException) {
        var cause = e.cause
        while (cause is InvocationTargetException) {
            cause = cause.cause
        }
        when (cause) {
            is LexerException -> {
                if (shouldLogInternalErrors) cause.printStackTrace()
                println(cause.message)
                exitProcess(1)
            }

            is ParserException -> {
                if (shouldLogInternalErrors) cause.printStackTrace()
                println(cause.message)
                exitProcess(1)
            }

            else -> {
                throw e
            }
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

fun printerr(message: String?) {
    System.err.println(message)
}
