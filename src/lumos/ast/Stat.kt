package lumos.ast

import lumos.Env
import lumos.token.TokenPos
import org.bytedeco.llvm.LLVM.LLVMValueRef

open class ExprStat(
    override val pos: TokenPos,
    val expr: Expr,
) : Stat {
    override val type: Type = expr.type

    override fun codegen(env: Env): LLVMValueRef {
        return expr.codegen(env)
    }
}

class IfStat(
    override val pos: TokenPos,
    val cond: Expr,
    val ifBody: Stat,
    val elseBody: Stat,
) : Stat {
    override val type: Type = VoidType(pos)

    override fun codegen(env: Env): LLVMValueRef {
        TODO("Not yet implemented")
    }
}

class WhileStat(
    override val pos: TokenPos,
    val cond: Expr,
    val body: Stat,
) : Stat {
    override val type: Type = VoidType(pos)

    override fun codegen(env: Env): LLVMValueRef {
        TODO("Not yet implemented")
    }
}

class DoWhileStat(
    override val pos: TokenPos,
    val cond: Expr,
    val body: Stat,
) : Stat {
    override val type: Type get() = TODO("Not yet implemented")

    override fun codegen(env: Env): LLVMValueRef {
        TODO("Not yet implemented")
    }
}

class LeaveStat(pos: TokenPos, expr: Expr) : ExprStat(pos, expr) {
    override fun codegen(env: Env): LLVMValueRef {
        TODO()
        return expr.codegen(env)
    }
}

class BreakStat(pos: TokenPos, expr: Expr) : ExprStat(pos, expr) {
    override fun codegen(env: Env): LLVMValueRef {
        TODO()
        return expr.codegen(env)
    }
}

class ReturnStat(pos: TokenPos, expr: Expr) : ExprStat(pos, expr) {
    override fun codegen(env: Env): LLVMValueRef {
        TODO()
    }
}

class ContinueStat(
    override val pos: TokenPos,
) : Stat {
    override val type: Type = VoidType(pos)

    override fun codegen(env: Env): LLVMValueRef {
        TODO()
    }
}
