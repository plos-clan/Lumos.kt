package lumos.parser

import lumos.ast.Block
import lumos.ast.IfStat
import lumos.ast.VoidType
import lumos.logger.internalError
import lumos.token.TokenType

fun Parser.parseKeyword() {
    val pos = lexpeek().pos
    check(lexpeek().type == TokenType.Kwd)
//    when (lexpeek().raw) {
//        "if" -> {
//            lexget()
//            val cond = parseExpr()
//            val if_block = Block(container)
//            IfStat(pos, container, cond, if_block)
//            container = if_block
//        }
//
//        "else" -> {
//            lexget()
//            if (lexpeek().raw == "if") {
//                lexget()
//                val cond = parseExpr()
//                val else_if_block = Block(container)
//                container.parent?.append(IfStat(container, cond, else_if_block))
//                container = else_if_block
//            } else {
//                val else_block = Block(container)
//                container.parent?.append((container, else_block))
//                container = else_block
//            }
//        }
//
//        "while" -> {
//            lexget()
//            val cond = parseExpr()
//            val while_block = Block(container)
//            container.append(While(container, cond, while_block))
//            container = while_block
//        }
//
//        "for" -> {
//            lexget()
//            val init = parseExpr()
//            val cond = parseExpr()
//            val step = parseExpr()
//            val for_block = Block(container)
//            container.append(For(container, init, cond, step, for_block))
//            container = for_block
//        }
//
//        "return" -> {
//            lexget()
//            val expr = parseExpr()
//            container.append(Return(container, expr))
//        }
//
//        "break" -> {
//            lexget()
//            container.append(Break(container))
//        }
//
//        "continue" -> {
//            lexget()
//            container.append(Continue(container))
//        }
//
//        "var" -> {
//            lexget()
//            val name = lexget().raw
//            val var_type = VoidType
//            val node = Var(container, name, var_type)
//            container.append(node)
//        }
//
//        else -> internalError("不应该出现的关键字 ${lexpeek().raw}")
//    }
}
