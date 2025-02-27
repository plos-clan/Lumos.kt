package lumos.lexer

data class LexerPos(
    val file: String,
) {
    var idx: Int = 0
    var line: Int = 1 // 当前行数
    var col: Int = 1 // 当前列数
    var newLine: Boolean = true

    fun update(text: String) {
        idx += text.length
        col += text.length
        for (i in text.indices) {
            if (text[i] == '\n') {
                newLine = true
                line++
                col = text.length - i
            } else if(newLine && !text[i].isWhitespace()) {
                newLine = false
            }
        }
    }

    override fun toString(): String {
        return "[$file:$line:$col]"
    }
}
