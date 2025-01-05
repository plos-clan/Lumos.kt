package lumos.ast

import lumos.Env
import lumos.logger.internalError
import lumos.token.TokenPos
import lumos.util.encodeString

// 字面量
interface Literal : Expr

class StringLiteral(
    override val pos: TokenPos,
    val value: String,
) : Literal {
    override val type = StringType(pos)

    override fun codegen(env: Env) {
        TODO()
    }

    override fun toString(): String {
        return "StringLiteral: '${encodeString(value)}'"
    }
}

class FmtString(
    override val pos: TokenPos,
    override val parent: Container,
) : Container, Literal {
    override val type = StringType(pos)
    val fmtData: MutableList<Expr> = mutableListOf()

    override fun find(name: String): AST? {
        return parent.find(name)
    }

    override fun findChild(name: String): AST? = null

    override fun append(ast: AST) {
        if (ast !is Expr) {
            internalError("FmtString can only contain Expr")
        }
        fmtData.add(ast)
    }

    override fun codegen(env: Env) {
        TODO()
    }

    override fun toString(): String {
        return "FmtString"
    }
}

class UndefinedValue(
    override val pos: TokenPos,
) : Literal {
    override val type = VoidType(pos)

    override fun codegen(env: Env) {
        TODO()
    }
}
