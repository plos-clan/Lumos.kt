package lumos.ast

import lumos.Env
import lumos.token.TokenPos

interface Container : AST {
    val parent: Container?
    val root: Root get() = parent?.root ?: this as Root

    // 在当前节点和父节点中寻找指定名字的子节点
    fun find(name: String): AST?
    // 在当前节点中寻找指定名字的子节点
    fun findChild(name: String): AST?
    fun findPath(path: String): AST? {
        var node: AST? = if (path.startsWith("::")) root else this
        val parts = (if (path.startsWith("::")) path.substring(2) else path).split('.')
        if (parts.isEmpty()) return node
        node = (node as Container).find(parts[0])
        for (part in parts.subList(1, parts.size)) {
            if (node !is Container) return null
            node = node.findChild(part)
        }
        return node
    }

    fun append(ast: AST) // 函数中不应该访问 ast
}

// 匿名容器（类型）
abstract class UnnamedContainer(
    override val parent: Container,
) : Container {
    init {
        parent.append(this)
    }
}

// 具名容器（类型）
abstract class NamedContainer(
    override val parent: Container,
    override val name: String,
) : Container, Named {
    init {
        parent.append(this)
    }
}

// 根节点
class Root(
    override val pos: TokenPos,
) : Container {
    override val parent = null

    private val asts = mutableListOf<AST>()

    override fun find(name: String): AST? {
        return asts.find { it is Named && it.name == name }
    }

    override fun findChild(name: String): AST? {
        TODO("Not yet implemented")
    }

    override fun append(ast: AST) {
        asts.add(ast)
    }

    override fun codegen(env: Env) {
        asts.forEach { it.codegen(env) }
    }
}
