package lumos.ast

import lumos.Env
import lumos.token.TokenPos

interface Operator : Expr

class BinaryOp(
    override val pos: TokenPos,
    val op: String,
    val left: Expr,
    val right: Expr,
) : Operator {
    override val type: Type
        get() = TODO("Not yet implemented")

    override fun codegen(env: Env) {
        TODO("Not yet implemented")
    }
}
