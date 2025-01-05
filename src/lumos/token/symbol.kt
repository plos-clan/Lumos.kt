package lumos.token

// 标识符中不允许的字符
val invalidSymChars = "~!@#$%^&*()[]{}-+=<>?/,.:;|\\'\"` ".toCharArray().toSet()

val keywords = listOf(
    listOf("var", "val", "let", "lit", "obj", "fn"),
    listOf("using"),
    listOf("in", "at", "to", "as", "of"),
    listOf("if", "elif", "else", "then", "do", "while", "until", "for", "break", "continue", "return"),
).flatten().toSet()
