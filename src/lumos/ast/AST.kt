package lumos.ast

import lumos.Env
import lumos.token.TokenPos
import java.io.PrintStream

// AST 节点
interface AST {
    val pos: TokenPos
    fun codegen(env: Env): Any
    fun dump(indent: Int = 2, depth: Int = 0) = dump(System.out, indent, depth)
    fun dump(ps: PrintStream, indent: Int = 2, depth: Int = 0) {
        ps.print(" ".repeat(indent * depth))
        ps.println(this.javaClass.name.split(".").last())
    }
}

interface Expr : AST {
    val type: Type
}

// 可被名称重整的
interface Manglingable : AST {
    fun mangling(): String?
}

interface Named : Manglingable {
    val name: String
    val parent: Container
}

// 语句直接继承表达式
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
