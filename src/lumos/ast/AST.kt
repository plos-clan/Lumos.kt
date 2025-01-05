package lumos.ast

import lumos.Env
import lumos.logger.internalError
import lumos.token.TokenPos
import lumos.token.invalidTokenPos
import java.io.PrintStream

interface AST {
    val pos: TokenPos
    fun codegen(env: Env)
    fun dump(indent: Int = 2, depth: Int = 0) = dump(System.out, indent, depth)
    fun dump(ps: PrintStream, indent: Int = 2, depth: Int = 0) {
        ps.print(" ".repeat(indent * depth))
        ps.println(this.javaClass.name.split(".").last())
    }
}

interface Expr : AST {
    val type: Type
}

interface Manglingable : AST {
    fun mangling(): String?
}

interface Named : Manglingable {
    val name: String
    val parent: Container
}

interface Stat : Expr

class Template(
    override val pos: TokenPos,
) : AST {
    private val asts = mutableListOf<AST>()

    fun append(ast: AST) {
        asts.add(ast)
    }

    override fun codegen(env: Env) {
        TODO()
    }
}

// 非法的 AST，用于标记错误
object InvalidAST : AST, NamedContainer(InvalidAST, "") {
    override val pos = invalidTokenPos

    override fun mangling() = "\$\$Invalid"

    override fun find(name: String) = InvalidAST

    override fun findChild(name: String) = InvalidAST

    override fun append(ast: AST) {}

    override fun codegen(env: Env) {
        internalError("InvalidAST should not be codegen")
    }
}
