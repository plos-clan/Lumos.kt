package lumos.lexer

import lumos.logger.internalError
import lumos.token.*

// 尝试解析空格
fun Lexer.trySpace(): Token? {
    val s = text.takeWhile { it.isWhitespace() }
    if (lexerStack.peek() == '$' && s.contains('\n')) {
        lexerStack.pop()
        if (lexerStack.peek() == '`') {
            logger.warning("格式化字符串的格式化表达式不应该跨行", tokpos)
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
            logger.error("多行注释没有结尾", tokpos)
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
        val s = match("""#([^\n]|\\\n)*""") ?: internalError("不应该出现的错误")
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
        val s = match("""([^\\\$`]|\\.)+""") ?: internalError("不应该出现的错误")
        if (lexerStack.peek() == '`' && s.contains('\n')) {
            logger.warning("格式化字符串不应该跨行", tokpos)
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
        it ?: logger.fatal("原始字符串字面量没有结尾", tokpos)
        return token(TokenType.Str, it.length)
    }
    if (text.startsWith("\"\"\"")) {
        val it = match("""######|###(?s).*?[^\\]###""".replace('#', '"'))
        it ?: logger.fatal("多行字符串字面量没有结尾", tokpos)
        return token(TokenType.Str, it.length)
    }
    if (text.startsWith("'")) {
        val it = match("""##|#(?s).*?[^\\]#""".replace('#', '\''))
        it ?: logger.fatal("字符字面量没有结尾", tokpos)
        if (it.contains('\n')) {
            logger.error("字符字面量不应该跨行", tokpos)
        }
        return token(TokenType.Chr, it.length)
    }
    if (text.startsWith("\"")) {
        val it = match("""##|#(?s).*?[^\\]#""".replace('#', '"'))
        it ?: logger.fatal("字符串字面量没有结尾", tokpos)
        if (it.contains('\n')) {
            logger.error("字符串字面量不应该跨行", tokpos)
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
        if (lexerStack.pop() != '(') throw Exception("括号不匹配")
        return token(TokenType.Punc, 1)
    }
    if (text[0] == ']') {
        if (lexerStack.pop() != '[') throw Exception("括号不匹配")
        return token(TokenType.Punc, 1)
    }
    if (text[0] == '}') {
        if (lexerStack.pop() != '{') throw Exception("括号不匹配")
        return token(TokenType.Punc, 1)
    }
    return null
}
