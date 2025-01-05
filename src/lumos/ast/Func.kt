package lumos.ast

import lumos.Env
import lumos.token.TokenPos

class Func(
    override val pos: TokenPos,
    override val parent: Container,
    val type: FuncType,
) : Container {
    var body: Block = Block(pos, this) // TODO 使用正确的 pos

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

class NamedFunc(
    override val pos: TokenPos,
    parent: Container,
    name: String,
) : NamedContainer(parent, name) {
    private var funcMap: Map<FuncType, Func> = mapOf()

    fun addFunc(func: Func) {
        funcMap += func.type to func
    }

    fun bestFunc(funcType: FuncType): Func? {
        return funcMap[funcType]
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
