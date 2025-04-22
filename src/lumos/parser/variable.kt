package lumos.parser

import lumos.ast.*
import lumos.token.*

fun Parser.addMutableVariable(names: List<Token>, _type: Type?, _init: Expr?, external: Boolean = true) {
    if (_type == null && _init == null) {
        logger.fatal("可变变量 $names 没有类型和初始值", lexpeek().pos)
    }
    val type = _type ?: _init?.type ?: VoidType(emptyTokenPos)
    val init = _init ?: _type!!.init ?: BinaryZero(emptyTokenPos, _type!!)
    for (tok in names) {
        Variable(tok.pos, container, tok.text, type, external = external, mutable = true)
    }
}

fun Parser.addImmutableVariable(names: List<Token>, _type: Type?, _init: Expr?, external: Boolean = true) {
    if (_init == null) {
        logger.fatal("不可变变量 $names 没有类型和初始值", lexpeek().pos)
    }
    val type = _type ?: _init.type
    val init = _init
    for (tok in names) {
        Variable(tok.pos, container, tok.text, type, external = external, mutable = false)
    }
}

fun Parser.parseVariable() {
    val pos = lexpeek().pos
    check(lexpeek() == KWD_VAL || lexpeek() == KWD_VAR)
    val isVal = lexget() == KWD_VAL

    val names: MutableList<Token> = mutableListOf()
    while (true) { // 收集变量名列表
        if (lexpeek().type != TokenType.Sym) {
            logger.fatal("变量名必须是标识符", lexpeek().pos)
        }
        if ((lexpeek().data as SymData).parent != container) {
            logger.fatal("变量名不能在嵌套命名空间中", lexpeek().pos)
        }
        logger.debug("变量名 ${lexpeek().raw}")
        names.add(lexget())

        when (lexpeek()) { // 遇到类型或初始化列表
            KWD_AS -> {
                lexget()
                val type = parseType()
                val init = if (lexpeek() == TOKEN_ASSIGN) {
                    lexget()
                    parseExpr()
                } else {
                    null
                }
                if (isVal) {
                    addImmutableVariable(names, type, init)
                } else {
                    addMutableVariable(names, type, init)
                }
            }

            KWD_BY -> {
                if (lexpeek().type == TokenType.Type) {
                    TODO()
                }
            }

            TOKEN_ASSIGN -> {
                lexget()
                val init = parseExpr()
                if (isVal) {
                    addImmutableVariable(names, null, init)
                } else {
                    addMutableVariable(names, null, init)
                }
            }

            TOKEN_COMMA -> {}

            TOKEN_SEMICOLON -> break

            else -> logger.fatal("变量声明后必须有类型或初始值", lexpeek().pos)
        }
    }
    if (names.size > 0) {
        logger.fatal("变量声明后必须有类型或初始值", lexpeek().pos)
    }
}
