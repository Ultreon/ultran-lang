package com.ultreon.ultranlang

interface ILogger {
    fun error(msg: Any?)
    fun warn(msg: Any?)
    fun info(msg: Any?)
    fun debug(msg: Any?)
}
