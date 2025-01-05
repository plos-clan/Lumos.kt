package lumos.token

import lumos.ast.AST
import lumos.ast.Container
import lumos.logger.internalError
import lumos.util.decodeString
import lumos.util.encodeString

open class TokenData {
    open val text: String = ""
    open fun encode(): String? = null
}

class SpaceData(raw: String) : TokenData() {
    val indent: Int

    init {
        var indent = 0
        for (c in raw) {
            when (c) {
                ' ' -> indent += 1
                '\t' -> indent += 4
                '\n' -> indent = 0
            }
        }
        this.indent = indent
    }
}

class CommentData(raw: String) : TokenData() {
    override val text: String = if (raw.startsWith("//")) {
        raw.substring(2).trim()
    } else {
        raw.substring(2, raw.length - 2).trimEnd()
    }
}

class NumData(raw: String) : TokenData() {
    var isFloat: Boolean = false
}

class StrData(raw: String) : TokenData() {
    override val text: String = if (raw.startsWith("'''")) {
        raw.substring(3, raw.length - 3)
    } else decodeString(
        if (raw.startsWith("\"\"\"")) {
            raw.substring(3, raw.length - 3)
        } else {
            raw.substring(1, raw.length - 1)
        }
    )

    override fun encode(): String {
        return "\'${encodeString(text)}\'"
    }
}

data class SymData(
    val name: String, // 符号名
    val isAbsolute: Boolean, // 是否是绝对路径
    val parent: Container? = null, // 父节点
    val sym: AST? = null, // 符号对应的 AST
) : TokenData() {
    override val text get() = name
}

class OpData(raw: String) : TokenData() {
    override val text = operatorNames[raw] ?: raw

    init {
        invalidSymChars.any { text.contains(it) } && internalError("找不到运算符 $raw")
    }
}

class OpNameData(raw: String) : TokenData() {
    override val text = raw.substring(1)
}

data class FmtData(
    override val text: String,
) : TokenData()
