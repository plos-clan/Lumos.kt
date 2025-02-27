package lumos.parser.parse

import lumos.parser.Parser
import lumos.parser.parseExpr

// 解析结构体字面量
//   struct { 1, 2, 3 }
//   struct { .a = 1, .b = 2, .c = 3 }
fun Parser.parseStructLiteral(): lumos.ast.ClassType {
    check(lexget().raw == "struct")
    val pos = lexpeek().pos
    check(lexget().raw == "{")
    val struct = lumos.ast.ClassType(pos, container)
    inContainer(struct) {
        while (lexpeek().raw != "}") {
            if (lexpeek().raw == ".") {
                val name = lexget().raw
                check(lexget().raw == "=")
                val expr = parseExpr()
            } else {
                val expr = parseExpr()

            }
            check(lexpeek().raw == "," || lexpeek().raw == "}")
            if (lexpeek().raw == ",") lexget()
        }
    }

    logger.fatal("未实现", lexpeek().pos)
}

fun Parser.parseStruct(): lumos.ast.ClassType {
    TODO()
}
