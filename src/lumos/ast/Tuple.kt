package lumos.ast

import lumos.Env
import lumos.logger.internalError
import lumos.token.TokenPos

class TupleType(
    override val pos: TokenPos,
) : Type {
    val elements: MutableList<Type> = mutableListOf()

    override val size get() = elements.sumOf { it.size }

    override val prefixOps: Map<String, (Expr) -> Expr>
        get() = TODO("Not yet implemented")
    override val postfixOps: Map<String, (Expr) -> Expr>
        get() = TODO("Not yet implemented")
    override val binaryOps: Map<String, (Expr, Expr) -> Expr>
        get() = TODO("Not yet implemented")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TupleType
        return elements == other.elements
    }

    override fun hashCode(): Int {
        return elements.hashCode()
    }

    fun append(type: Type) {
        elements.add(type)
    }

    override fun mangling(): String? {
        TODO("Not yet implemented")
    }

    override fun codegen(env: Env) {
        elements.forEach { it.codegen(env) }
    }
}

class Tuple(
    override val pos: TokenPos,
    parent: Container,
) : UnnamedContainer(parent) {
    private val _elements: MutableList<Expr> = mutableListOf()
    val type = TupleType(pos)
    val elements: List<Expr>
        get() = _elements

//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//        other as Tuple
//        return elements == other.elements
//    }

    override fun find(name: String): AST? {
        return parent.find(name)
    }

    override fun findChild(name: String): AST? {
        return null
    }

    override fun append(ast: AST) {
        if (ast !is Expr) {
            internalError("Tuple can only contain Expr")
        }
        _elements.add(ast)
        type.append(ast.type)
    }

    override fun codegen(env: Env) {
        elements.forEach { it.codegen(env) }
    }
}
