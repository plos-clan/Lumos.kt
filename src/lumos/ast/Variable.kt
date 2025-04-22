package lumos.ast

import lumos.Env
import lumos.token.TokenPos

fun formatName(name: String): String {
    return "${name.length}${name}"
}

class Variable(
    override val pos: TokenPos,
    override val parent: Container,
    override val name: String,
    val type: Type,
    var init: Expr? = null,
    var external: Boolean = true,
    var mutable: Boolean = false,
) : Named {

    init {
        parent.append(this)
    }

    override fun mangling(): String? {
        if (parent !is NamedContainer) return null
        val parent_mangling = parent.mangling() ?: return null
        TODO()
    }

    override fun codegen(env: Env) {
        TODO("Not yet implemented")
    }
}
