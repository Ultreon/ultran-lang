package com.ultreon.ultranlang

import com.soywiz.korio.file.std.*
import com.ultreon.ultranlang.ast.Program
import com.ultreon.ultranlang.classes.ULClasses
import com.ultreon.ultranlang.error.LexerException
import com.ultreon.ultranlang.error.ParserException
import com.ultreon.ultranlang.error.SemanticException
import com.ultreon.ultranlang.func.NativeCalls
import com.ultreon.ultranlang.io.cwd
import kotlin.native.CName
import kotlin.properties.Delegates

var errorCode by Delegates.notNull<Int>()
lateinit var classes: ULClasses
lateinit var calls: NativeCalls

internal suspend fun internalMain(args: Array<String>): Int {
    val flagNames = mutableListOf<String>()
    val flagChars = mutableListOf<Char>()
    val keywords = mutableMapOf<String, String>()
    val arguments = mutableListOf<String>()

    for (arg in args) {
        if (arg.startsWith("--")) {
            if ('=' in arg) {
                val split = arg.split('=')
                val key = split[0]
                val value = split[0]
                keywords[key] = value
            } else {
                flagNames.add(arg.substring(2))
            }
        } else if (arg.startsWith("-") && arg.length >= 2) {
            flagChars.add(arg[1])
        } else {
            arguments.add(arg)
        }
    }

    shouldLogScope = flagNames.remove("scope")
    shouldLogStack = flagNames.remove("stack")
    shouldLogTokens = flagNames.remove("tokens")
    shouldLogDebug = flagNames.remove("debug") || flagChars.remove('d')
    shouldLogInternalErrors = flagNames.remove("internal-errors")

    if (flagNames.isNotEmpty()) {
        logger.error("Unknown flags: ${flagNames.joinToString(", ")}")
        return 1
    }

    if (arguments.isEmpty()) {
        logger.error("No arguments provided")
        return 1
    }

    val inputFile = localVfs(cwd())[arguments[0]]

    if (!inputFile.exists()) {
        logger.error("File not found: ${inputFile.absolutePath}")
        return 1
    }

    val text = inputFile.readString()

    init()

    eval(text)
    return 0
}

/**
 * Evaluate a script.
 */
@CName("eval")
fun eval(text: String) {
    val lexer = Lexer(text)
    val tree: Program
    try {
        val parser = Parser(lexer)
        tree = parser.parse()
    } catch (e: LexerException) {
        if (shouldLogInternalErrors) e.printStackTrace()
        logger.error(e.message)
        errorCode = 1
        return
    } catch (e: ParserException) {
        if (shouldLogInternalErrors) e.printStackTrace()
        logger.error(e.message)
        errorCode = 1
        return
    } catch (e: Exception) {
        var cause = e.cause
        while (cause is Exception) {
            cause = cause.cause
        }
        when (cause) {
            is LexerException -> {
                if (shouldLogInternalErrors) cause.printStackTrace()
                logger.error(cause.message)
                errorCode = 1
                return
            }

            is ParserException -> {
                if (shouldLogInternalErrors) cause.printStackTrace()
                logger.error(cause.message)
                errorCode = 1
                return
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
        errorCode = 1
    } catch (e: Exception) {
        var cause = e.cause
        while (cause is Exception) {
            cause = cause.cause
            if (cause is SemanticException) {
                cause.printStackTrace()
                logger.error(cause.message)
                errorCode = 1
                return
            } else if (cause == null) {
                errorCode = 1
                throw e
            }
        }
    }

    val interpreter = Interpreter(tree)
    interpreter.interpret()
    errorCode = 0
}

@Deprecated("Replaced", ReplaceWith("logger.error(message)"))
fun printErr(message: String?) {
    logger.error(message)
}

@CName("init")
fun init() {
    calls = NativeCalls().also { it.loadDefaults() }
    classes = ULClasses().also { it.loadDefaults() }
    Runtime.init(LaunchProperties())
}
