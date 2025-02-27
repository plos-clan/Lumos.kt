package lumos.util

enum class Visiable { Public, Protect, Private }

class Attribute {
    var export: Boolean = false
    var inline: Boolean = false
    var static: Boolean = false
    var visiable: Visiable = Visiable.Protect

    var ownershipReturns: String? = null
    var ownershipTakes: MutableList<Pair<Int, String>> = mutableListOf()
    var ownershipHolds: MutableList<Pair<Int, String>> = mutableListOf()

}

fun Attribute.check(): String? {
    if (inline && export) {
        return "inline 和 export 不能同时设置"
    }
    return null
}
