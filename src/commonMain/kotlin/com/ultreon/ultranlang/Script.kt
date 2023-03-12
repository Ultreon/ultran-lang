package com.ultreon.ultranlang

import com.soywiz.korio.file.VfsFile
import com.ultreon.ultranlang.ast.Program
import com.ultreon.ultranlang.classes.ULClasses
import com.ultreon.ultranlang.error.LexerException
import com.ultreon.ultranlang.error.ParserException
import com.ultreon.ultranlang.error.SemanticException
import com.ultreon.ultranlang.func.NativeCalls
import kotlinx.coroutines.runBlocking

@Suppress("unused")
class Script(val code: String) {
    val calls: NativeCalls = NativeCalls()
    val classes: ULClasses = ULClasses()

    constructor(file: VfsFile) : this(runBlocking { file.readString() })

    fun execute(args: Array<String>): Int {
        val lexer = Lexer(code)
        val tree: Program
        try {
            val parser = Parser(lexer)
            tree = parser.parse()
        } catch (e: LexerException) {
            if (shouldLogInternalErrors) e.printStackTrace()
            logger.error(e.message)
            return 1
        } catch (e: ParserException) {
            if (shouldLogInternalErrors) e.printStackTrace()
            logger.error(e.message)
            return 1
        } catch (e: Exception) {
            var cause = e.cause
            while (cause is ExecutionException) {
                when (cause) {
                    is LexerException -> {
                        if (shouldLogInternalErrors) cause.printStackTrace()
                        logger.error(cause.message)
                        return 1
                    }

                    is ParserException -> {
                        if (shouldLogInternalErrors) cause.printStackTrace()
                        logger.error(cause.message)
                        return 1
                    }
                }
                cause = cause.cause
            }

            throw e
        }

        val semanticAnalyzer = SemanticAnalyzer(calls = calls, classes = classes)

        try {
            semanticAnalyzer.visit(tree)
        } catch (e: SemanticException) {
            e.printStackTrace()
            logger.error(e.message)
            return 1
        } catch (e: Exception) {
            var cause = e.cause
            while (cause is Exception) {
                if (cause is SemanticException) {
                    cause.printStackTrace()
                    logger.error(cause.message)
                    return 1
                }
                cause = cause.cause
            }
            throw e
        }

        val interpreter = Interpreter(tree)
        interpreter.interpret()
        return 0
    }
}