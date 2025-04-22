package lumos.ast

import lumos.Env
import lumos.helper.encodeString
import lumos.logger.internalError
import lumos.token.TokenPos
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM.LLVMConstString

// 字面量
interface Literal : Expr

class StringLiteral(
    override val pos: TokenPos,
    val value: String,
) : Literal {
    override val type = StringType(pos)
    val length = value.length

    private val llvmRef: LLVMValueRef = LLVMConstString(value, length, 1)

    override fun codegen(env: Env): LLVMValueRef {
        return llvmRef
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
    private val fmtData: MutableList<Expr> = mutableListOf()

    private var llvmRef: LLVMValueRef? = null

    override fun find(name: String): AST? {
        return parent.find(name)
    }

    override fun findChild(name: String): AST? = null

    override fun append(ast: AST) {
        if (ast !is Expr) internalError("FmtString can only contain Expr")
        fmtData.add(ast)
    }

    override fun codegen(env: Env): LLVMValueRef {
        llvmRef != null && return llvmRef!!
        TODO()
        return llvmRef!!
    }

    override fun toString(): String {
        return "FmtString"
    }
}

// 字面量 `undefined`
class UndefinedValue(
    override val pos: TokenPos,
) : Literal {
    override val type = VoidType(pos)

    override fun codegen(env: Env): LLVMValueRef {
        TODO()
    }
}

// 任何类型的二进制 0
class BinaryZero(
    override val pos: TokenPos,
    override val type: Type,
) : Literal {

    override fun codegen(env: Env): LLVMValueRef {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return "BinaryZero"
    }
}
