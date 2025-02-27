package lumos.parser

import lumos.logger.internalError
import lumos.token.Token
import lumos.token.TokenType
import lumos.util.l10n

fun Parser.parseIf() {
    val pos = lexpeek().pos
    check(lexget() == Token(TokenType.Kwd, "if"))
    TODO()
}

// while (expr) { ... } 条件循环
fun Parser.parseWhile() {
    val pos = lexpeek().pos
    check(lexget() == Token(TokenType.Kwd, "while"))
    TODO()
}

// for (init; cond; step) { ... } 计数循环
// for (var i in 0..10) { ... } 遍历循环
fun Parser.parseFor() {
    val pos = lexpeek().pos
    check(lexget() == Token(TokenType.Kwd, "for"))
    TODO()
}

// loop { ... } 无限循环
fun Parser.parseLoop() {
    val pos = lexpeek().pos
    check(lexget() == Token(TokenType.Kwd, "loop"))
    val stat = parseStat()
    TODO("语法树添加 loop 类型")
}

// 退出代码块
// val a = {
//     leave 1;
// }
fun Parser.parseLeave() {
    val pos = lexpeek().pos
    check(lexget() == Token(TokenType.Kwd, "leave"))
    val stat = parseStat()
    TODO()
}

// 退出循环
// loop {
//     break;
// }
fun Parser.parseBreak() {
    val pos = lexpeek().pos
    check(lexget() == Token(TokenType.Kwd, "break"))
    val stat = parseStat()
    TODO()
}

// 退出函数
fun Parser.parseReturn() {
    val pos = lexpeek().pos
    check(lexget() == Token(TokenType.Kwd, "return"))
    val stat = parseStat()
    TODO()
}

fun Parser.parseContinue() {
    TODO()
}

fun Parser.parseVariable() {
    TODO()
}

fun Parser.parseKeyword() {
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
}
