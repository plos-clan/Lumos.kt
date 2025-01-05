package lumos.ast

import lumos.Env
import lumos.logger.internalError
import lumos.token.TokenPos

class Namespace(
    override val pos: TokenPos,
    parent: Container,
    name: String,
) : NamedContainer(parent, name) {
    init {
        if (parent !is Namespace && parent !is Root) {
            internalError("Namespace parent must be a Namespace")
        }
    }

    override fun find(name: String): AST? {
        TODO("Not yet implemented")
    }

    override fun findChild(name: String): AST? {
        TODO("Not yet implemented")
    }

    override fun append(ast: AST) {
        TODO("Not yet implemented")
    }

    override fun codegen(env: Env) {

        TODO("Not yet implemented")
    }

    override fun mangling(): String? {
        TODO("Not yet implemented")
    }

}
