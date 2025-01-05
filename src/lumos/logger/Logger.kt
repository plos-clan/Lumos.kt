package lumos.logger

import lumos.token.TokenPos
import java.io.OutputStream
import java.io.PrintStream
import kotlin.system.exitProcess

class Logger(
    private val stream: PrintStream,
    var level: LogLevel = LogLevel.Info,
) {
    val count = LogLevel.entries.associateWith { 0 }.toMutableMap()
    var useTabs = false

    constructor(stream: OutputStream, level: LogLevel = LogLevel.Info) : this(PrintStream(stream), level)

    fun print(message: Any) {
        stream.print(message)
    }

    fun print(message: Any, fg: LogColor? = null, bg: LogColor? = null) {
        stream.print((fg?.fg() ?: "") + (bg?.bg() ?: "") + message + LogColor.Reset)
    }

    fun println(message: Any) {
        stream.println(message)
    }

    fun println(message: Any, fg: LogColor? = null, bg: LogColor? = null) {
        stream.println((fg?.fg() ?: "") + (bg?.bg() ?: "") + message + LogColor.Reset)
    }

    fun log(level: LogLevel, message: String, pos: TokenPos? = null) {
        count[level] = count[level]!! + 1
        if (this.level <= level) {
            print("[$level]", level.fg, level.bg)
            if (pos != null) {
                print(" at [${pos.file}:${pos.line}:${pos.col}]")
            }
            print(if (useTabs) ":\t" else ": ")
            println(message, LogColor.Purple)
        }
    }

    fun log(level: LogLevel, message: Any, pos: TokenPos? = null) {
        log(level, message.toString(), pos)
    }

    fun debug(message: String, pos: TokenPos? = null) {
        log(LogLevel.Debug, message, pos)
    }

    fun debug(message: Any, pos: TokenPos? = null) {
        debug(message.toString(), pos)
    }

    fun info(message: String, pos: TokenPos? = null) {
        log(LogLevel.Info, message, pos)
    }

    fun info(message: Any, pos: TokenPos? = null) {
        info(message.toString(), pos)
    }

    fun warning(message: String, pos: TokenPos? = null) {
        log(LogLevel.Warning, message, pos)
    }

    fun warning(message: Any, pos: TokenPos? = null) {
        warning(message.toString(), pos)
    }

    fun error(message: String, pos: TokenPos? = null) {
        log(LogLevel.Error, message, pos)
    }

    fun error(message: Any, pos: TokenPos? = null) {
        error(message.toString(), pos)
    }

    fun fatal(message: String, pos: TokenPos? = null): Nothing {
        log(LogLevel.Fatal, message, pos)
        exitProcess(1)
    }

    fun fatal(message: Any, pos: TokenPos? = null): Nothing {
        fatal(message.toString(), pos)
    }

    fun internalError(e: Exception): Nothing {
        e.printStackTrace()
        internalError()
    }

    fun internalError(message: String? = null, pos: TokenPos? = null): Nothing {
        println("这不是你的错，是我的错。", LogColor.Purple)
        println("请向 Yan.Huang24@student.xjtlu.edu.cn 报告这个错误。", LogColor.Purple)
        if (message != null) fatal("内部错误：$message", pos)
        exitProcess(1)
    }

    fun check(final: Boolean = false) {
        if (count[LogLevel.Error]!! > 0 || count[LogLevel.Fatal]!! > 0) {
            println("失败：", LogColor.Red)
            println("产生了 ${count[LogLevel.Info]!!} 个提示，${count[LogLevel.Warning]!!} 个警告，${count[LogLevel.Error]!!} 个错误，${count[LogLevel.Fatal]!!} 个致命错误")
            exitProcess(1)
        }
        if (final) {
            println("成功：", LogColor.Green)
            println("产生了 ${count[LogLevel.Info]!!} 个提示，${count[LogLevel.Warning]!!} 个警告，${count[LogLevel.Error]!!} 个错误，${count[LogLevel.Fatal]!!} 个致命错误")
            exitProcess(0)
        }
    }
}
