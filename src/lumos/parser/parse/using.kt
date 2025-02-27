package lumos.parser.parse

import lumos.ast.Namespace
import lumos.parser.Parser
import lumos.token.TokenType

// using 命名空间 -> 命名空间;
// using "模块" -> 命名空间;
fun Parser.parseUsing(): Namespace {
    check(lexget().raw == "using")
    val tok = lexget()
    if (tok.type == TokenType.Sym) {
        val src = container.findPath(tok.raw) ?: logger.fatal("找不到命名空间 ${tok.raw}", tok.pos)

    } else if (tok.type == TokenType.Str) {
        if (container !== root) logger.fatal("只能在根命名空间中导入模块", tok.pos)
        val name = tok.raw

        // val module = env.openModule(name)
        // if (module == null) {
        //     logger.error("找不到模块 $name", tok.pos)
        //     return
        // }
        // container.append(module)
    }
    logger.fatal("using 后应该是模块名或字符串", tok.pos)
}
