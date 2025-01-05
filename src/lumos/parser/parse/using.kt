package lumos.parser.parse

import env
import lumos.ast.Namespace
import lumos.parser.Parser
import lumos.token.TokenType

fun Parser.parseUsing(): Namespace {
    check(lexget().raw == "using")
    val tok = lexget()
    check(tok.type == TokenType.Sym)
    val name = tok.raw
    val module = env.openModule(name)
    if (module == null) {
        logger.error("找不到模块 $name", tok.pos)
        return
    }
    container.append(module)
}
