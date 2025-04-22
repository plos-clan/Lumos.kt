package lumos.ast

import lumos.Env
import lumos.token.TokenPos
import org.bytedeco.llvm.LLVM.LLVMValueRef
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

// 表达式
interface Expr : AST {
    val type: Type
    override fun codegen(env: Env): LLVMValueRef
}

// 可被名称重整的
//     1. 用户可以自己起名的类型和函数等
//     2. 内置的类型
interface Manglingable : AST {
    // 如果可以被重整，返回重整后的名称
    // 否则返回 null
    fun mangling(): String?
}

// 用户可以自己起名的类型和函数等
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
