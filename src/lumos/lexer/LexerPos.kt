package lumos.lexer

data class LexerPos(
    val file: String,
    var idx: Int,
    var line: Int, // 当前行数
    var col: Int, // 当前列数
) {
    constructor(file: String) : this(file, 0, 1, 1)

    fun update(text: String) {
        idx += text.length
        col += text.length
        for (i in text.indices) {
            if (text[i] == '\n') {
                line++
                col = text.length - i
            }
        }
    }

    override fun toString(): String {
        return "[$file:$line:$col]"
    }
}
