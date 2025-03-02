package lumos.ast

import lumos.Env
import lumos.token.TokenPos

open class ExprStat(
    override val pos: TokenPos,
    val expr: Expr,
) : Stat {
    override val type: Type = expr.type

    override fun codegen(env: Env) {
        expr.codegen(env)
    }
}

class IfStat(
    override val pos: TokenPos,
    val cond: Expr,
    val ifBody: Stat,
    val elseBody: Stat,
) : Stat {
    override val type: Type = VoidType(pos)

    override fun codegen(env: Env) {
        TODO("Not yet implemented")
    }
}

class WhileStat(
    override val pos: TokenPos,
    val cond: Expr,
    val body: Stat,
) : Stat {
    override val type: Type = VoidType(pos)

    override fun codegen(env: Env) {
        TODO("Not yet implemented")
    }
}

class DoWhileStat(
    override val pos: TokenPos,
    val cond: Expr,
    val body: Stat,
) : Stat {
    override val type: Type get() = TODO("Not yet implemented")

    override fun codegen(env: Env) {
        TODO("Not yet implemented")
    }
}

class LeaveStat(pos: TokenPos, expr: Expr) : ExprStat(pos, expr) {
    override fun codegen(env: Env) {
        TODO()
    }
}

class BreakStat(pos: TokenPos, expr: Expr) : ExprStat(pos, expr) {
    override fun codegen(env: Env) {
        TODO()
    }
}

class ReturnStat(pos: TokenPos, expr: Expr) : ExprStat(pos, expr) {
    override fun codegen(env: Env) {
        TODO()
    }
}

class ContinueStat(
    override val pos: TokenPos,
) : Stat {
    override val type: Type = VoidType(pos)

    override fun codegen(env: Env) {
        TODO()
    }
}
