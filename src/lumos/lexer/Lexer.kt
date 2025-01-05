package lumos.lexer

import lumos.FileData
import lumos.logger.Logger
import lumos.logger.defaultLogger
import lumos.token.Token
import lumos.token.TokenPos
import lumos.token.TokenType
import java.util.*

// lexerStack 状态：
//     '/' 根状态
//     '`' 格式化字符串
//     '~' 多行格式化字符串
//     '$' 格式化表达式
//     '(' 小括号内
//     '[' 中括号内
//     '{' 大括号内

class Lexer(
    file: String,
    private var _text: String,
    val logger: Logger = defaultLogger,
) {
    private var pos = LexerPos(file)
    private var _tok: Token? = null
    val lexerStack: Stack<Char> = Stack()
    val skipTokenTypes = listOf(TokenType.Space, TokenType.Comment)
    private val skippedToken = Token(TokenType.Inv, "")

    var text: String
        get() = _text
        private set(value) {
            _text = value
        }

    val tokpos: TokenPos get() = TokenPos(pos)

    constructor(file: FileData, logger: Logger = defaultLogger) : this(file.relativePath, file.text, logger)

    init {
        lexerStack.push('/')
    }

    fun Lexer.match(regex: String): String? {
        return regex.toRegex().matchAt(text, 0)?.value
    }

    fun Lexer.match(regex: Regex): String? {
        return regex.matchAt(text, 0)?.value
    }

    // 从 lexer 的当前位置提取出一个 n 字符的 token
    fun token(type: TokenType, n: Int): Token {
        assert(type != TokenType.Inv)
        assert(n > 0)
        val s = text.substring(0, n)
        text = text.substring(n)
        val beginPos = TokenPos(pos)
        pos.update(s)
        val endPos = TokenPos(pos)
        val tok = Token(type, s, beginPos, endPos)
        if (skipTokenTypes.contains(type)) return skippedToken
        logger.debug(tok, tok.pos)
        return tok
    }

    fun textSkip(n: Int) {
        assert(n > 0)
        val s = text.substring(0, n)
        text = text.substring(n)
        pos.update(s)
    }

    private val tryParseFuncs = listOf(
        ::tryFmtStr, // 这个必须第一个执行
        ::tryStr,
        ::trySpace,
        ::tryComment,
        ::tryMacro,
        ::tryPunc,
        ::tryNum,
        ::tryRootNS,
        ::tryTemplate,
        ::tryOp,
        ::tryOpName,
        ::trySym,
    )

    private fun tokenEof(): Token {
        if (lexerStack.peek() != '/') {
            if (lexerStack.peek() == '`') throw Exception("文件结束但格式化字符串未结束")
            if (lexerStack.peek() == '~') throw Exception("文件结束但多行格式化字符串未结束")
            if (lexerStack.peek() == '$') throw Exception("文件结束但格式化表达式未结束")
            throw Exception("文件结束但缺失匹配的括号")
        }
        return Token(TokenType.Eof, "", pos, pos)
    }

    // 从 text 中提取下一个 token
    private fun next(): Token {
        var tok: Token? = skippedToken
        while (tok == skippedToken) {
            if (text.isEmpty()) return tokenEof()
            for (func in tryParseFuncs) {
                tok = func()
                if (tok == skippedToken) break
                if (tok != null) return tok
            }
        }
        throw Exception("未知的序列，前 10 字符为：${text.substring(0, 10)}")
    }

    fun peek(): Token {
        if (_tok == null) _tok = next()
        return _tok as Token
    }

    fun get(): Token {
        if (_tok == null) return next()
        val tmp = _tok
        _tok = null
        return tmp as Token
    }

    fun peek(type: TokenType): Token? {
        return if (peek().type == type) peek() else null
    }

    fun peek(raw: String): Token? {
        return if (peek().raw == raw) peek() else null
    }

    fun get(type: TokenType): Token? {
        return if (peek().type == type) get() else null
    }

    fun get(raw: String): Token? {
        return if (peek().raw == raw) get() else null
    }

    fun xget(type: TokenType): Token {
        return get(type) ?: throw Exception("期望 $type，但是得到 ${peek().type}")
    }

    fun xget(raw: String): Token {
        return get(raw) ?: throw Exception("期望 $raw，但是得到 ${peek().raw}")
    }

    fun fatal(): Nothing {
        logger.fatal("", tokpos)
    }
}
