package lumos.parser

import lumos.ast.Template
import lumos.helper.l10n
import lumos.token.TOKEN_EOF
import lumos.token.TokenType

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
