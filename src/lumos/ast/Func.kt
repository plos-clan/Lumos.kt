package lumos.ast

import lumos.Env
import lumos.token.TokenPos

// 匿名函数或函数重载
class Func(
    override val pos: TokenPos,
    override val parent: Container,
    val type: FuncType,
) : Container {
    var body: Block = Block(pos, this) // TODO 使用正确的 pos
    var overload: Boolean = true // 函数是否启用了重载
    var inline: Boolean = false

    var defined: Boolean = false // 是否已经定义过

    override fun find(name: String): AST? {
        return body.find(name)
    }

    override fun findChild(name: String): AST? {
        return body.findChild(name)
    }

    override fun append(ast: AST) {
        return body.append(ast)
    }

    override fun codegen(env: Env) {
        TODO("Not yet implemented")
    }
}

fun findBestFunc(funcs: Map<FuncType, Func>, type: FuncType): Func? {
    return funcs[type]

}

// 具名函数，内含多个函数体（重载）
//   注意具名函数内可以有一个不启用重载的函数体
//   其余函数体均必须启用重载
class NamedFunc(
    override val pos: TokenPos,
    parent: Container,
    name: String,
) : NamedContainer(parent, name) {
    private val funcMap: MutableMap<FuncType, Func> = mutableMapOf()
    val noOverloads: Boolean get() = funcMap.size == 1

    fun addFunc(func: Func) {
        funcMap += func.type to func
    }

    fun bestFunc(funcType: FuncType): Func? {
        return findBestFunc(funcMap, funcType)
    }

    override fun find(name: String): AST? {
        return parent.find(name)
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
