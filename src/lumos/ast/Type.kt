package lumos.ast

import lumos.Env
import lumos.token.TokenPos
import lumos.token.invalidTokenPos

interface Type : Manglingable {
    val size: Int // 类型占用的字节数
    val prefixOps: Map<String, (Expr) -> Expr>
    val postfixOps: Map<String, (Expr) -> Expr>
    val binaryOps: Map<String, (Expr, Expr) -> Expr>
}

interface CustomizeType : Type {
    override var size: Int
    override val prefixOps: MutableMap<String, (Expr) -> Expr>
    override val postfixOps: MutableMap<String, (Expr) -> Expr>
    override val binaryOps: MutableMap<String, (Expr, Expr) -> Expr>
}

abstract class EmptyType : Type {
    override val size: Int = 0 // 类型占用的字节数
    override val prefixOps: Map<String, (Expr) -> Expr> = mapOf()
    override val postfixOps: Map<String, (Expr) -> Expr> = mapOf()
    override val binaryOps: Map<String, (Expr, Expr) -> Expr> = mapOf()
}

class StringType(
    override val pos: TokenPos,
) : EmptyType() {
    override fun mangling(): String {
        return "string"
    }

    override fun codegen(env: Env) {
        TODO()
    }

    override val prefixOps: Map<String, (Expr) -> Expr> = mapOf()
    override val postfixOps: Map<String, (Expr) -> Expr> = mapOf()
    override val binaryOps: Map<String, (Expr, Expr) -> Expr> = mapOf()
}

class IntType(
    override val pos: TokenPos,
    val nbits: Int = 32,
) : EmptyType() {
    override val size get() = nbits / 8

    override fun mangling(): String {
        return "i${nbits}"
    }

    override fun codegen(env: Env) {
        TODO()
    }

    companion object {
        val _prefixOps: Map<String, (Expr) -> Expr> = mapOf()
        val _postfixOps: Map<String, (Expr) -> Expr> = mapOf()
        val _binaryOps: Map<String, (Expr, Expr) -> Expr> = mapOf()
    }

    override val prefixOps get() = _prefixOps
    override val postfixOps get() = _postfixOps
    override val binaryOps get() = _binaryOps
}

class VoidType(
    override val pos: TokenPos,
) : EmptyType() {
    override val size = 1 // 我们让 void 类型占用一个字节，便于 void * 指针的计算

    override fun mangling(): String {
        return "v"
    }

    override fun codegen(env: Env) {
        TODO()
    }
}

class ClassType(
    override val pos: TokenPos,
    parent: Container,
) : UnnamedContainer(parent), CustomizeType {
    override var size: Int = 0

    var var_map: MutableMap<String, NamedType> = mutableMapOf()
    var var_list: MutableList<NamedType> = mutableListOf()

    var fn_map: MutableMap<String, NamedFunc> = mutableMapOf()
    var fn_list: MutableList<NamedFunc> = mutableListOf()

    override fun mangling(): String {
        TODO()
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
        TODO()
    }

    override val prefixOps: MutableMap<String, (Expr) -> Expr> = mutableMapOf()
    override val postfixOps: MutableMap<String, (Expr) -> Expr> = mutableMapOf()
    override val binaryOps: MutableMap<String, (Expr, Expr) -> Expr> = mutableMapOf()
}

data class FuncType(
    override val pos: TokenPos,
    val returnType: Type,
    val paramsType: TupleType,
) : EmptyType() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FuncType
        return returnType == other.returnType && paramsType == other.paramsType
    }

    // 判断两个函数类型是否冲突
    // 两个函数类型冲突的条件是：参数类型相同
    infix fun conflictsWith(other: FuncType): Boolean {
        return this === other || paramsType == other.paramsType
    }

    override fun mangling(): String {
        TODO()
    }

    override fun codegen(env: Env) {
        TODO()
    }

    override fun hashCode(): Int {
        return 31 * returnType.hashCode() + paramsType.hashCode()
    }

    companion object {
        val _binaryOps: Map<String, (Expr, Expr) -> Expr> =
            mapOf("call" to { lhs, rhs -> UndefinedValue(invalidTokenPos) })
    }

    override val binaryOps: Map<String, (Expr, Expr) -> Expr> get() = _binaryOps
}
