package lumos.ast

import lumos.Env
import lumos.token.TokenPos
import org.bytedeco.llvm.LLVM.LLVMTypeRef

class NamedType(
    override val pos: TokenPos,
    override val parent: Container,
    override val name: String,
    val reftype: Type,
) : Named, Type {
    override val llvmRef: LLVMTypeRef = TODO()
    override val size get() = reftype.size
    override val prefixOps get() = reftype.prefixOps
    override val postfixOps get() = reftype.postfixOps
    override val binaryOps get() = reftype.binaryOps
    override val implicitConvertFrom: List<Type>
        get() = TODO("Not yet implemented")
    override val implicitConvertTo: List<Type>
        get() = TODO("Not yet implemented")
    override val explicitConvertFrom: List<Type>
        get() = TODO("Not yet implemented")
    override val explicitConvertTo: List<Type>
        get() = TODO("Not yet implemented")
    override val constructor: Map<FuncType, Func>
        get() = TODO("Not yet implemented")
    override val destructor: Func?
        get() = TODO("Not yet implemented")

    init {
        parent.append(this)
    }

    override fun mangling(): String? {
        if (parent !is NamedContainer) return null
        TODO()
    }

    override fun codegen(env: Env): LLVMTypeRef {
        TODO()
    }
}
