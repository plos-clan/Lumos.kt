package lumos.helper

import lumos.ast.Expr

class PrefixOpMap : HashMap<String, (Expr) -> Expr>()
class PostfixOpMap : HashMap<String, (Expr) -> Expr>()
class BinaryOpMap : HashMap<String, (Expr, Expr) -> Expr>()

fun prefixOpMapOf(vararg pairs: Pair<String, (Expr) -> Expr>): PrefixOpMap {
    return PrefixOpMap().apply { putAll(pairs) }
}

fun postfixOpMapOf(vararg pairs: Pair<String, (Expr) -> Expr>): PostfixOpMap {
    return PostfixOpMap().apply { putAll(pairs) }
}

fun binaryOpMapOf(vararg pairs: Pair<String, (Expr, Expr) -> Expr>): BinaryOpMap {
    return BinaryOpMap().apply { putAll(pairs) }
}
