package lumos.ast

import lumos.Env
import lumos.helper.l10n
import lumos.logger.internalError
import lumos.token.TokenPos

// 代码块
class Block(
    override val pos: TokenPos,
    parent: Container,
) : UnnamedContainer(parent), Stat {
    val vars: MutableMap<String, Variable> = mutableMapOf()
    val code: MutableList<Stat> = mutableListOf()

    override val type: Type get() = code.lastOrNull()?.type ?: VoidType(pos)

    override fun find(name: String): AST? {
        return vars[name] ?: parent.find(name)
    }

    override fun findChild(name: String): AST? {
        return vars[name]
    }

    override fun append(ast: AST) {
        when (ast) {
            is Variable -> vars[ast.name] = ast
            is Stat -> code.add(ast)
            else -> internalError("Block can only contain Variable or Stat")
        }
    }

    override fun codegen(env: Env) {
        TODO(l10n("error.no-impl"))
    }
}
