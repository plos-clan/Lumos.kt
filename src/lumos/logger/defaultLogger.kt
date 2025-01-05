package lumos.logger

import debug
import lumos.token.TokenPos

val defaultLogger = Logger(System.out, if (debug) LogLevel.Debug else LogLevel.Info)

fun internalError(e: Exception): Nothing {
    defaultLogger.internalError(e)
}

fun internalError(message: String? = null, pos: TokenPos? = null): Nothing {
    defaultLogger.internalError(message, pos)
}
