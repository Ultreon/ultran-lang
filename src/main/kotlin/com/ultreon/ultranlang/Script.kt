package com.ultreon.ultranlang

import com.ultreon.ultranlang.ast.Program
import com.ultreon.ultranlang.error.LexerException
import com.ultreon.ultranlang.error.ParserException
import com.ultreon.ultranlang.error.SemanticException
import com.ultreon.ultranlang.func.NativeCalls
import java.io.File
import java.lang.reflect.InvocationTargetException
import kotlin.system.exitProcess

class Script(file: File) {
    val calls: NativeCalls = NativeCalls()
    private val text: String = file.readText()

    fun execute(args: Array<String>) {
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

        val semanticAnalyzer = SemanticAnalyzer(calls = calls)

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
}