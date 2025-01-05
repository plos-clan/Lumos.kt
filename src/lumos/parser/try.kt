package lumos.parser

import lumos.ast.AST

fun Parser.tryFunc() : AST? {
    if (lexpeek().raw != "fn") return null
    return parseFunc()
}
