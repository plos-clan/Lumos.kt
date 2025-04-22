package lumos.parser

import lumos.ast.*
import lumos.token.Token
import lumos.token.TokenType

// 解析 if/while 的条件表达式
fun Parser.parseCondExpr(): Expr {
    return TupleBooleanAll(parseTuple())
}

fun Parser.parseIf(): IfStat {
    val pos = lexpeek().pos
    check(lexget() == Token(TokenType.Kwd, "if"))
    val cond = parseCondExpr()
    val ifBody = parseStat()
    val elseBody = when (lexpeek() == Token(TokenType.Kwd, "else")) {
        true -> {
            lexget()
            parseStat()
        }

        false -> ExprStat(pos, UndefinedValue(pos))
    }
    return IfStat(pos, cond, ifBody, elseBody)
}

// while (expr) { ... } 条件循环
fun Parser.parseWhile(): WhileStat {
    val pos = lexpeek().pos
    check(lexget() == Token(TokenType.Kwd, "while"))
    val cond = parseCondExpr()
    val body = parseBlock()
    return WhileStat(pos, cond, body)
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
    val stat = parseExprStatOrBlock()
    TODO("语法树添加 loop 类型")
}

// 退出代码块
// val a = {
//     leave 1;
// }
fun Parser.parseLeave() {
    val pos = lexpeek().pos
    check(lexget() == Token(TokenType.Kwd, "leave"))
    val stat = parseExprStatOrBlock()
    TODO()
}

// 退出循环
// loop {
//     break;
// }
fun Parser.parseBreak() {
    val pos = lexpeek().pos
    check(lexget() == Token(TokenType.Kwd, "break"))
    val stat = parseExprStatOrBlock()
    TODO()
}

// 退出函数
fun Parser.parseReturn() {
    val pos = lexpeek().pos
    check(lexget() == Token(TokenType.Kwd, "return"))
    val stat = parseExprStatOrBlock()
    TODO()
}

fun Parser.parseContinue() {
    val pos = lexpeek().pos
    check(lexget() == Token(TokenType.Kwd, "continue"))

    TODO()
}
