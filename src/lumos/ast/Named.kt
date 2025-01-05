package lumos.ast

import lumos.Env
import lumos.token.TokenPos

class NamedType(
    override val pos: TokenPos,
    override val parent: Container,
    override val name: String,
    val reftype: Type,
) : Named, Type {
    override val size get() = reftype.size
    override val prefixOps get() = reftype.prefixOps
    override val postfixOps get() = reftype.postfixOps
    override val binaryOps get() = reftype.binaryOps

    init {
        parent.append(this)
    }

    override fun mangling(): String? {
        TODO()
    }

    override fun codegen(env: Env) {}
}
