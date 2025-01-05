package lumos.token

import lumos.lexer.LexerPos

val invalidTokenPos = TokenPos("",-1,0,0)

data class TokenPos(
    val file: String,
    val idx: Int,
    val line: Int, // 当前行数
    val col: Int, // 当前列数
)  {
    constructor(lp: LexerPos) : this(lp.file, lp.idx, lp.line, lp.col)

    override fun toString(): String {
        return "[$file:$line:$col]"
    }
}
