package lumos.ast

import lumos.Env
import lumos.token.TokenPos
import org.bytedeco.llvm.LLVM.LLVMValueRef

interface Operator : Expr

class BinaryOp(
    override val pos: TokenPos,
    val op: String,
    val left: Expr,
    val right: Expr,
) : Operator {
    override val type: Type
        get() = TODO("Not yet implemented")

    override fun codegen(env: Env): LLVMValueRef {
        TODO("Not yet implemented")
    }
}
