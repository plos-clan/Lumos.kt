package lumos.helper

import lumos.ast.Container

data class Attribute(val container: Container) {
    var export: Boolean = false
    var inline: Boolean = false
    var static: Boolean = false
    var visible: Visible = Visible.Protect

    var ownershipReturns: String? = null
    var ownershipTakes: MutableList<Pair<Int, String>> = mutableListOf()
    var ownershipHolds: MutableList<Pair<Int, String>> = mutableListOf()

    fun check(): String? {
        inline && export && return "inline 和 export 不能同时设置"
        return null
    }
}
