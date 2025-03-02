package lumos.token

import lumos.logger.internalError

class OperatorType {
    private var _prefix: Boolean = false
    private var _binary: Boolean = false
    private var _postfix: Boolean = false

    var prefix: Boolean
        get() = _prefix
        set(value) {
            _prefix = value
        }

    var binary: Boolean
        get() = _binary
        set(value) {
            _binary = value
            _binary && _postfix && throw Exception("Operator can't be both binary and postfix")
        }

    var postfix: Boolean
        get() = _postfix
        set(value) {
            _postfix = value
            _binary && _postfix && throw Exception("Operator can't be both binary and postfix")
        }
}

val prefixOperatorList = arrayListOf(
    arrayListOf("+", "-", "~", "!", "*", "&", "++", "--"),
).flatten().sortedByDescending { it.length }

val postfixOperatorList = arrayListOf(
    arrayListOf("++", "--"),
).flatten().sortedByDescending { it.length }

val binaryOperatorList = arrayListOf(
    arrayListOf("+", "-", "*", "/", "%"),
    arrayListOf("&", "|", "^", "<<", ">>", "&&", "||", "^^"),
    arrayListOf("=", "+=", "-=", "*=", "/=", "%="),
    arrayListOf("&=", "|=", "^=", "<<=", ">>=", "&&=", "||=", "^^="),
    arrayListOf("->"),
    arrayListOf("?:"),
).flatten().sortedByDescending { it.length }

val operatorList = arrayListOf(
    prefixOperatorList, postfixOperatorList, binaryOperatorList,
).flatten().distinct().sortedByDescending { it.length }

val prefixOperators = prefixOperatorList.toSet()
val postfixOperators = postfixOperatorList.toSet()
val binaryOperators = binaryOperatorList.toSet()
val operators = operatorList.toSet()

val keywordOperators = setOf(
    "sizeof", // 对于一个变量，获取它所占的内存大小
    "lengthof", // 对于一个数组，获取它的元素个数
    "typeof", // 获取类型
    "typenameof", // 获取类型名
)

val operatorNames = arrayListOf(
    mapOf("+" to "add", "-" to "sub", "*" to "mul", "/" to "div", "%" to "mod"),
    mapOf("&&" to "and", "||" to "or", "^^" to "xor"),
    mapOf("&" to "band", "|" to "bor", "^" to "bxor", "<<" to "shl", ">>" to "shr"),
    mapOf("=" to "set", "+=" to "iadd", "-=" to "isub", "*=" to "imul", "/=" to "idiv", "%=" to "imod"),
    mapOf("&=" to "iband", "|=" to "ibor", "^=" to "ibxor", "<<=" to "ishl", ">>=" to "ishr"),
    mapOf("->" to "to"),
    mapOf("==" to "eq", "!=" to "ne", "<" to "lt", "<=" to "le", ">" to "gt", ">=" to "ge", "<=>" to "cmp"),
    mapOf("===" to "seq", "!==" to "sne", "$==" to "beq", "$!=" to "bne"),
).reduce { acc, map -> acc + map }

val operatorTypes = operatorNames.mapValues { (k, v) ->
    val type = OperatorType()
    if (k !in operators) internalError("Unknown operator $k")
    if (k in prefixOperators) type.prefix = true
    if (k in postfixOperators) type.postfix = true
    if (k in binaryOperators) type.binary = true
    type
}
