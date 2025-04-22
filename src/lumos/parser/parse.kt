package lumos.parser

import lumos.ast.*
import lumos.logger.internalError
import lumos.token.*
import lumos.helper.l10n
import java.util.*

// 完整解析一个语句
//     val a = 1;
//     a += 2;
//     { println a; }
fun Parser.parseStat(): Stat {
    if (lexpeek() == Token(TokenType.Punc, ";")) {
        val pos = lexget().pos
        return ExprStat(pos, UndefinedValue(pos))
    }
    if (lexpeek() == Token(TokenType.Punc, "{")) return parseBlock()
    val pos = lexpeek().pos
    val ast: Stat = when (lexpeek().type) {
        TokenType.Kwd -> parseKeyword()
        TokenType.Type -> parseVarDecl()
        else -> ExprStat(pos, parseExpr())
    }
    if (lexpeek() != Token(TokenType.Punc, ";")) {
        logger.error("表达式后应该有分号")
    } else {
        lexget()
    }
    return ast
}

// 解析一个表达式语句或代码块，开头不能是关键字或类型
// 简单的封装来复用代码
//     a + b;
//     { a + b; }
fun Parser.parseExprStatOrBlock(): Stat {
    if (lexpeek() == Token(TokenType.Punc, ";")) {
        val pos = lexget().pos
        return ExprStat(pos, UndefinedValue(pos))
    }
    if (lexpeek() == Token(TokenType.Punc, "{")) return parseBlock()
    val pos = lexpeek().pos
    val ast = ExprStat(pos, parseExpr())
    if (lexpeek() != Token(TokenType.Punc, ";")) {
        logger.error("表达式后应该有分号")
    } else {
        lexget()
    }
    return ast
}

// 解析一个代码块
//     { a + b; }
fun Parser.parseBlock(): Block {
    val pos = lexpeek().pos
    check(lexget() == Token(TokenType.Punc, "{"))
    val block = Block(pos, container)
    inContainer(block) {
        while (!lexeof && lexpeek() != Token(TokenType.Punc, "}")) {
            it.append(parseStat())
        }
    }
    check(lexget() == Token(TokenType.Punc, "}"))
    return block
}

// 解析代码块表达式
//     val { a + b; }
fun Parser.parseBlockExpr(): Block {
    val pos = lexpeek().pos
    check(lexget() == Token(TokenType.Kwd, "val"))
    check(lexget() == Token(TokenType.Punc, "{"))
    val block = Block(pos, container)
    inContainer(block) {
        it.append(parseExpr())
    }
    return block
}

fun Parser.parseFunc(): AST {
    val pos = lexpeek().pos
    check(lexget().raw == "fn")
    var parent = container
    if (lexpeek().type == TokenType.Sym) {
        val tok = lexget()
        if ((tok.data as SymData).isAbsolute) logger.error("函数名不能以 :: 开头")
        val name = lexget().raw
        val result = container.findChild(name)
        if (result != null && result !is NamedFunc) throw Exception("符号 $name 已经存在")
        if (result == null) NamedFunc(pos, container, name)
        parent = container.findChild(name) as NamedFunc
    }
    parseType()
    val rettype = VoidType(invalidTokenPos)
    val fntype = FuncType(pos, rettype, TupleType(invalidTokenPos))
    val func = Func(pos, parent, fntype)
    inContainer(func) {
        it as Func
        it.body = parseBlock()
    }
    if (parent is NamedFunc) outofContainer()
    parent.append(func)
    return func
}

// 解析一个类型，例如：
//     int
//     int*
//     typeof(1 + 1)
fun Parser.parseType(): Type {
    if (lexpeek() == Token(TokenType.Op, "typeof")) {
        lexget()
        val node = parseExprWithoutInfix()
        return node.type
    }
    if (lexpeek().type == TokenType.Type) {
        val tok = lexget()
        logger.error("找不到类型 ${tok.raw}，用 void 替代")
        return VoidType(tok.pos)
    }
    if (lexpeek().type == TokenType.Sym) {
        val tok = lexget()
        logger.error("找不到类型 ${tok.raw}，用 void 替代")
        return VoidType(tok.pos)
    }
    internalError("不应该出现的错误")
}

// 尝试解析一个类型，如果无法解析则返回 null
fun Parser.tryType(): Type? {
    if (lexpeek().type != TokenType.Type && lexpeek() != Token(TokenType.Op, "typeof")) return null
    return parseType()
}

fun Parser.parseVarDecl(): Stat {
    val pos = lexpeek().pos
    check(lexget().raw == "var")
    val name = lexget().raw
    val varType = tryType()
//    val node = Var(container, name, var_type)
//    container.append(node)
    TODO()
}

// 变量声明
fun Parser.tryVarDecl(): Boolean {
    if (lexpeek().raw != "var") return false
    parseVarDecl()
    return true
}

// 解析一个元组，例如：
//     (1, 2, 3)
// 只要遇到括号就会调用这个函数
// 不管里面有多少个元素，都会解析到一个 Tuple 对象中
//     (1) + 1  这种情况下左边的 1 会被封装到 Tuple 中
fun Parser.parseTuple(): Tuple {
    val pos = lexpeek().pos
    check(lexget().raw == "(")
    val tuple = Tuple(pos, container)
    inContainer(tuple) {
        while (lexpeek() != TOKEN_RIGHT_PAREN) {
            it.append(parseExpr())
            if (lexpeek() == TOKEN_RIGHT_PAREN) break
            if (lexpeek().raw == ",") lexget()
        }
    }
    if (tuple.elements.isEmpty()) {
        logger.error(l10n("parser.error.empty-tuple"), pos)
        tuple.append(UndefinedValue(pos))
    }
    check(lexget().raw == ")")
    return tuple
}

// 解析一个不包含二元运算符的表达式
// 比如 -1 或 i++ 这种
// 也可以是非二元运算符和括号
// 比如 -(a - 1)
fun Parser.parseExprWithoutInfix(): Expr {
    val pos = lexpeek().pos
    check(lexpeek().type == TokenType.Op || lexpeek().type == TokenType.Sym)
    val prefixStack = Stack<Token>()
    while (lexpeek().type == TokenType.Op) {
        lexpeek().data as OpData
        prefixStack.push(lexget())
    }
    TODO()
}

// 解析任何表达式直到遇到分隔符
//     有效的分隔符如 ; , ) } 等
fun Parser.parseExpr(): Expr {
    val pos = lexpeek().pos
    val lhs = parseExprWithoutInfix()
    val op = lexget(TokenType.Op) ?: return lhs
    val rhs = parseExprWithoutInfix()
    BinaryOp(pos, op.data.text, lhs, rhs)
    TODO()
}

fun Parser.parseFmtStr(): FmtString {
    val pos = lexpeek().pos
    check(lexget().type == TokenType.FmtBegin)
    val parent = FmtString(pos, container)
    inContainer(parent) {
        while (lexpeek().type != TokenType.FmtEnd) {
            when (lexpeek().type) {
                TokenType.FmtData -> {
                    val tok = lexget()
                    it.append(StringLiteral(tok.pos, tok.raw))
                }

                TokenType.FmtExprBegin -> {
                    lexget()
                    it.append(parseExpr())
                    check(lexget().type == TokenType.FmtExprEnd)
                }

                else -> {
                    logger.error(l10n("error.maybe.lexer"))
                    internalError(l10n("parser.error.unexpected"), lexpeek().pos)
                }
            }
        }
    }
    return parent
}

// 什么 JB 玩意
//     </type T/>              T 为任意类型
//     </type T : Something/>  T 继承或实现 Something
//     </i32 N/>               N 为 i32 类型
fun Parser.parseTemplate(): Template {
    val pos = lexpeek().pos
    check(lexget().type == TokenType.TemplateBegin)
    val template = Template(pos)
    while (lexpeek().type != TokenType.TemplateEnd) {
        if (lexpeek() == TOKEN_EOF) {
            logger.fatal(l10n("parser.error.unexpected-eof"), lexpeek().pos)
        }
        TODO()
    }
    return template
}

fun Parser.parseClass(): ClassType {
    val pos = lexpeek().pos
    check(lexget() == KWD_CLASS)
    val name = if (lexpeek().type == TokenType.Sym) lexget().raw else ""
    val cls = ClassType(pos, container)
    inContainer(cls) {
        parseContainer()
    }
    if (name.isNotEmpty()) NamedType(pos, container, name, cls)
    return cls
}

fun Parser.parseContainer() {
    while (lexpeek().type != TokenType.Eof && lexpeek() != Token(TokenType.Punc, "}")) {
        when (lexpeek().type) {
            TokenType.FmtBegin -> parseFmtStr()

            TokenType.Kwd -> parseKeyword()

            in setOf(TokenType.Op, TokenType.Sym) -> {
                parseExpr()
            }

            else -> internalError(l10n("parser.error.unexpected"))
        }
    }
}
