package lumos.ast

import lumos.Env
import lumos.token.TokenPos

class Variable(
    override val pos: TokenPos,
    override val parent: Container,
    override val name: String,
    val type: Type,
) : Named {
    init {
        parent.append(this)
    }

    override fun mangling(): String? {
        TODO()
    }

    override fun codegen(env: Env) {}
}
