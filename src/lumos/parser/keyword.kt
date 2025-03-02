package lumos.parser

import lumos.ast.ExprStat
import lumos.ast.Stat
import lumos.ast.UndefinedValue
import lumos.logger.internalError
import lumos.token.TokenType
import lumos.helper.l10n

fun Parser.parseKeyword(): Stat {
    val pos = lexpeek().pos
    check(lexpeek().type == TokenType.Kwd)
    when (lexpeek().raw) {
        "if" -> parseIf()
        "else" -> logger.fatal(l10n("parser.error.unexpected-else"), lexpeek().pos)

        "while" -> parseWhile()
        "for" -> parseFor()
        "loop" -> parseLoop()

        "leave" -> parseLeave()
        "break" -> parseBreak()
        "return" -> parseReturn()
        "continue" -> parseContinue()

        "val" -> parseVariable()
        "var" -> parseVariable()

        else -> internalError("不应该出现的关键字 ${lexpeek().raw}")
    }
    return ExprStat(pos, UndefinedValue(pos))
}
