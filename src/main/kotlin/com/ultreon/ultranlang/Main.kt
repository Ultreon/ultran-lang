package com.ultreon.ultranlang

import com.ultreon.ultranlang.ast.Program
import com.ultreon.ultranlang.classes.ScriptClasses
import com.ultreon.ultranlang.error.LexerException
import com.ultreon.ultranlang.error.ParserException
import com.ultreon.ultranlang.error.SemanticException
import com.ultreon.ultranlang.func.NativeCalls
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
        logger.error("Unknown flags: ${flags.joinToString(", ")}")
        return
    }

    if (arguments.isEmpty()) {
        logger.error("No arguments provided")
        return
    }

    val inputFile = File(arguments[0])

    if (!inputFile.exists()) {
        logger.error("File not found: ${inputFile.absolutePath}")
        return
    }

    val text = inputFile.readText()

    //************************//
    //     Execute script     //
    //************************//
    val calls = NativeCalls().also { it.loadDefaults() }
    val classes = ScriptClasses().also { it.loadDefaults() }
    val lexer = Lexer(text)
    val tree: Program
    try {
        val parser = Parser(lexer)
        tree = parser.parse()
    } catch (e: LexerException) {
        if (shouldLogInternalErrors) e.printStackTrace()
        logger.error(e.message)
        exitProcess(1)
    } catch (e: ParserException) {
        if (shouldLogInternalErrors) e.printStackTrace()
        logger.error(e.message)
        exitProcess(1)
    } catch (e: InvocationTargetException) {
        var cause = e.cause
        while (cause is InvocationTargetException) {
            cause = cause.cause
        }
        when (cause) {
            is LexerException -> {
                if (shouldLogInternalErrors) cause.printStackTrace()
                logger.error(cause.message)
                exitProcess(1)
            }

            is ParserException -> {
                if (shouldLogInternalErrors) cause.printStackTrace()
                logger.error(cause.message)
                exitProcess(1)
            }

            else -> {
                throw e
            }
        }
    }

    val semanticAnalyzer = SemanticAnalyzer(calls = calls, classes = classes)

    try {
        semanticAnalyzer.visit(tree)
    } catch (e: SemanticException) {
        e.printStackTrace()
        logger.error(e.message)
        exitProcess(1)
    } catch (e: InvocationTargetException) {
        var cause = e.cause
        while (cause is InvocationTargetException) {
            cause = cause.cause
        }
        if (cause is SemanticException) {
            cause.printStackTrace()
            logger.error(cause.message)
            exitProcess(1)
        } else {
            throw e
        }
    }

    val interpreter = Interpreter(tree)
    interpreter.interpret()
}

@Deprecated("Replaced", ReplaceWith("logger.error(message)"))
fun printErr(message: String?) {
    logger.error(message)
}
