package lumos.token

val prefixOperators = arrayListOf("+", "-", "~", "!", "*", "&", "++", "--").sortedByDescending { it.length }

val postfixOperators = arrayListOf("++", "--").sortedByDescending { it.length }

val binaryOperators = arrayListOf(
    arrayListOf("+", "-", "*", "/", "%"),
    arrayListOf("&", "|", "^", "<<", ">>", "&&", "||", "^^"),
    arrayListOf("=", "+=", "-=", "*=", "/=", "%="),
    arrayListOf("&=", "|=", "^=", "<<=", ">>=", "&&=", "||=", "^^="),
    arrayListOf("->"),
    arrayListOf("?:"),
).flatten().sortedByDescending { it.length }

val operators = arrayListOf(
    prefixOperators, postfixOperators, binaryOperators,
).flatten().sortedByDescending { it.length }

val operatorKeywords = setOf(
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
