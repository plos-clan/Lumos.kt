package lumos.parser

import lumos.FileData
import lumos.ast.AST
import lumos.ast.Container
import lumos.ast.Root
import lumos.ast.Type
import lumos.lexer.Lexer
import lumos.logger.Logger
import lumos.logger.defaultLogger
import lumos.logger.internalError
import lumos.token.SymData
import lumos.token.Token
import lumos.token.TokenType

class Parser(
    val file: FileData,
    val logger: Logger = defaultLogger,
) {
    private val lexer = Lexer(file, logger)
    val root: Root = Root(lexer.tokpos)
    private var _container: Container = this.root
    var container: Container
        get() = _container
        private set(value) {
            _container = value
        }

    private var _tok: Token? = null // 用于缓存 peek 的 token

    // 解析一个符号，符号的格式为 [RootNS] Sym [. Sym]*
    //     a.b.c
    //     ::a.b.c
    // 如果符号不存在，则返回的 SymData 中 node 为 null
    // 如果符号的父节点不存在，则返回的 SymData 中 parent 为 null
    private fun parseSymbol(): Token {
        var tok = lexer.get()
        val pos = tok.pos
        check(tok.type == TokenType.Sym || tok.type == TokenType.RootNS)
        val isAbsolute = tok.type == TokenType.RootNS
        var parent: Container? = if (isAbsolute) container.root else container
        if (isAbsolute) {
            if (lexer.peek().type != TokenType.Sym) {
                return Token(TokenType.Sym, "::", tok.pos, tok.endPos, SymData("::", isAbsolute, null, parent))
            }
            tok = lexer.get()
        }
        var name = tok.raw
        var fullname = name
        var node: AST? = parent!!.find(name)
        while (lexer.peek().raw == ".") {
            lexer.get()
            check(lexer.peek().type == TokenType.Sym)
            if (node == null || node !is Container) {
                if (parent != null) {
                    logger.error(if (node == null) "找不到符号 $name" else "符号 $name 不是容器", tok.pos)
                    parent = null
                }
                continue
            }
            parent = node
            tok = lexer.get()
            name = tok.raw
            fullname += if (fullname.last() == ':') name else ".$name"
            node = parent.findChild(name)
        }
        val type = if (node != null && node is Type) TokenType.Type else TokenType.Sym
        return Token(type, fullname, pos, tok.endPos, SymData(name, isAbsolute, parent, node))
    }

    fun lexpeek(): Token {
        if (_tok != null) return _tok!!
        if (lexer.peek().type == TokenType.Sym || lexer.peek().type == TokenType.RootNS) {
            _tok = parseSymbol()
            return _tok!!
        }
        return lexer.peek()
    }

    fun lexget(): Token {
        if (_tok != null) {
            val tok = _tok!!
            _tok = null
            return tok
        }
        if (lexer.peek().type == TokenType.Sym || lexer.peek().type == TokenType.RootNS) {
            return parseSymbol()
        }
        return lexer.get()
    }

    fun lexpeek(type: TokenType): Token? {
        if (type == TokenType.RootNS) internalError("不应该出现 RootNS")
        if (type == TokenType.Sym) {
            return if (lexpeek().type == TokenType.Sym) lexpeek() else null
        }
        if (_tok != null) {
            return if (_tok!!.type == type) _tok else null
        }
        return lexer.peek(type)
    }

    fun lexpeek(raw: String): Token? {
        if (_tok != null) return if (_tok!!.raw == raw) _tok else null
        return lexer.peek(raw)
    }

    fun lexget(type: TokenType): Token? {
        if (type == TokenType.RootNS) internalError("不应该出现 RootNS")
        return lexer.get(type)
    }

    fun lexget(raw: String): Token? {
        return lexer.get(raw)
    }

    fun xlexget(type: TokenType): Token {
        if (type == TokenType.RootNS) internalError("不应该出现 RootNS")
        return lexer.xget(type)
    }

    fun xlexget(raw: String): Token {
        return lexer.xget(raw)
    }

    fun intoContainer(c: Container) {
        container = c
    }

    fun outofContainer() {
        container = container.parent ?: internalError("根节点无法退出")
    }

    fun inContainer(c: Container, func: (c: Container) -> Unit) {
        check(c.parent === container)
        container = c
        func(c)
        container = container.parent ?: internalError("根节点无法退出")
    }

    fun parse(): Root {
        check(lexer.tokpos.idx == 0)
        tryFunc()
        return root
    }
}
