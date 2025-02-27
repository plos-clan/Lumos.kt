package lumos.lexer

import lumos.logger.internalError
import lumos.token.*
import lumos.util.l10n

// 尝试解析空格
//     注意特例：
//     单行格式化字符串的格式化表达式不应该跨行
fun Lexer.trySpace(): Token? {
    val s = text.takeWhile { it.isWhitespace() }
    if (lexerStack.peek() == '$' && s.contains('\n')) {
        lexerStack.pop()
        if (lexerStack.peek() == '`') {
            logger.warning(l10n("lexer.warn.fmt-expr-cross-line"), tokpos)
        }
        lexerStack.push('$')
    }
    val n = s.length
    return if (n > 0) token(TokenType.Space, n) else null
}

// 尝试解析注释
fun Lexer.tryComment(): Token? { // bash 脚本注释
    if (text.startsWith("#!")) {
        val n = text.takeWhile { it != '\n' }.length
        return token(TokenType.Comment, n)
    } // 单行注释
    if (text.startsWith("//")) {
        val n = text.takeWhile { it != '\n' }.length
        return token(TokenType.Comment, n)
    } // 多行注释
    if (text.startsWith("/*")) {
        val endIndex = text.indexOf("*/")
        if (endIndex < 0) {
            logger.error(l10n("lexer.error.multi-line-comment-no-end"), tokpos)
            return token(TokenType.Comment, text.length)
        }
        return token(TokenType.Comment, endIndex + 2)
    }
    return null
}

// 尝试解析数字
fun Lexer.tryNum(): Token? {
    val s = match("""\d+(\.\d+)?""")
    return if (s != null) {
        token(TokenType.Num, s.length)
    } else {
        null
    }
}

// 尝试解析宏
fun Lexer.tryMacro(): Token? {
    if (text.startsWith("#")) {
        if (!newLine) logger.fatal(l10n("lexer.error.macro-not-at-line-start"), tokpos)
        val s = match("""#([^\n]|\\\n)*""") ?: internalError(l10n("lexer.error.unexpected"))
        return token(TokenType.Macro, s.length)
    }
    return null
}

// 尝试解析格式化字符串
fun Lexer.tryFmtStr(): Token? { // 在格式化表达式中
    if (lexerStack.peek() == '$') {
        if (text.startsWith("}")) {
            lexerStack.pop()
            return token(TokenType.FmtExprEnd, 1)
        }
        return null
    } // 在格式化字符串中
    if (lexerStack.peek() == '`' || lexerStack.peek() == '~') {
        if (lexerStack.peek() == '~') { // 多行
            if (text.startsWith("```")) {
                lexerStack.pop()
                return token(TokenType.FmtEnd, 3)
            }
        } else { // 单行
            if (text.startsWith("`")) {
                lexerStack.pop()
                return token(TokenType.FmtEnd, 1)
            }
        }
        if (text.startsWith("\${")) {
            lexerStack.push('$')
            return token(TokenType.FmtExprBegin, 2)
        }
        if (text.startsWith("\$")) {
            val tok = token(TokenType.FmtData, 1)
            return trySym() ?: tok
        }
        val s = match("""([^\\\$`]|\\.)+""") ?: internalError(l10n("lexer.error.unexpected"))
        if (lexerStack.peek() == '`' && s.contains('\n')) {
            logger.warning(l10n("lexer.warn.fmt-str-cross-line"), tokpos)
        }
        return token(TokenType.FmtData, s.length)
    } // 不在格式化字符串中
    if (text.startsWith("```")) {
        lexerStack.push('~')
        return token(TokenType.FmtBegin, 3)
    }
    if (text.startsWith("`")) {
        lexerStack.push('`')
        return token(TokenType.FmtBegin, 1)
    }
    return null
}

// 尝试解析字符串
fun Lexer.tryStr(): Token? {
    if (text.startsWith("'''")) {
        val it = match("""###(?s).*?###""".replace('#', '\''))
        it ?: logger.fatal(l10n("lexer.error.raw-str-no-end"), tokpos)
        return token(TokenType.Str, it.length)
    }
    if (text.startsWith("\"\"\"")) {
        val it = match("""######|###(?s).*?[^\\]###""".replace('#', '"'))
        it ?: logger.fatal(l10n("lexer.error.multi-line-str-no-end"), tokpos)
        return token(TokenType.Str, it.length)
    }
    if (text.startsWith("'")) {
        val it = match("""##|#(?s).*?[^\\]#""".replace('#', '\''))
        it ?: logger.fatal(l10n("lexer.error.char-literal-no-end"), tokpos)
        if (it.contains('\n')) {
            logger.error(l10n("lexer.error.char-literal-cross-line"), tokpos)
        }
        return token(TokenType.Chr, it.length)
    }
    if (text.startsWith("\"")) {
        val it = match("""##|#(?s).*?[^\\]#""".replace('#', '"'))
        it ?: logger.fatal(l10n("lexer.error.str-literal-no-end"), tokpos)
        if (it.contains('\n')) {
            logger.error(l10n("lexer.error.str-literal-cross-line"), tokpos)
        }
        return token(TokenType.Str, it.length)
    }
    return null
}

// 尝试解析标识符
fun Lexer.trySym(): Token? {
    if ("0123456789".contains(text[0])) return null
    val s = text.takeWhile { !invalidSymChars.contains(it) }
    if (s.isEmpty()) return null
    if (keywords.contains(s)) {
        return token(TokenType.Kwd, s.length)
    }
    if (operatorKeywords.contains(s)) { // 比如 sizeof
        return token(TokenType.Op, s.length)
    }
    return token(TokenType.Sym, s.length)
}

fun Lexer.tryRootNS(): Token? {
    return if (text.startsWith("::")) token(TokenType.RootNS, 2) else null
}

fun Lexer.tryTemplate(): Token? {
    if (text.startsWith("</")) return token(TokenType.TemplateBegin, 2)
    if (text.startsWith("/>")) return token(TokenType.TemplateEnd, 2)
    return null
}

// 尝试解析运算符，如 + - * / % 等
fun Lexer.tryOp(): Token? {
    for (op in operators) {
        if (text.startsWith(op)) return token(TokenType.Op, op.length)
    }
    return null
}

// 尝试解析运算符名称，如 \add
fun Lexer.tryOpName(): Token? {
    if (text[0] != '\\') return null
    if ("0123456789".contains(text[1])) return null
    val s = text.substring(1).takeWhile { !invalidSymChars.contains(it) }
    if (s.isEmpty()) return null
    return token(TokenType.OpName, s.length + 1)
}

// 尝试解析分隔符
fun Lexer.tryPunc(): Token? {
    if (",.;".contains(text[0])) {
        return token(TokenType.Punc, 1)
    }
    if (text[0] == '(') {
        lexerStack.push('(')
        return token(TokenType.Punc, 1)
    }
    if (text[0] == '[') {
        lexerStack.push('[')
        return token(TokenType.Punc, 1)
    }
    if (text[0] == '{') {
        lexerStack.push('{')
        return token(TokenType.Punc, 1)
    }
    if (text[0] == ')') {
        if (lexerStack.pop() != '(') throw Exception(l10n("lexer.error.bracket-mismatch"))
        return token(TokenType.Punc, 1)
    }
    if (text[0] == ']') {
        if (lexerStack.pop() != '[') throw Exception(l10n("lexer.error.bracket-mismatch"))
        return token(TokenType.Punc, 1)
    }
    if (text[0] == '}') {
        if (lexerStack.pop() != '{') throw Exception(l10n("lexer.error.bracket-mismatch"))
        return token(TokenType.Punc, 1)
    }
    return null
}
