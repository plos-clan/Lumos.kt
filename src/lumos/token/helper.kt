package lumos.token

val emptyTokenPos = TokenPos("", 0, 0, 0)

val emptyTokenData = TokenData()

val invalidTokenPos = TokenPos("", -1, 0, 0)

val TOKEN_EOF = Token(TokenType.Eof, "")
val TOKEN_LEFT_PAREN = Token(TokenType.Punc, "(")
val TOKEN_RIGHT_PAREN = Token(TokenType.Punc, ")")
val TOKEN_LEFT_BRACKET = Token(TokenType.Punc, "[")
val TOKEN_RIGHT_BRACKET = Token(TokenType.Punc, "]")
val TOKEN_LEFT_BRACE = Token(TokenType.Punc, "{")
val TOKEN_RIGHT_BRACE = Token(TokenType.Punc, "}")
val TOKEN_COMMA = Token(TokenType.Punc, ",")
val TOKEN_DOT = Token(TokenType.Punc, ".")
val TOKEN_COLON = Token(TokenType.Punc, ":")
val TOKEN_SEMICOLON = Token(TokenType.Punc, ";")

// 关键字
val TOKEN_CLASS = Token(TokenType.Kwd, "class")
val TOKEN_ELSE = Token(TokenType.Kwd, "else")
val TOKEN_FALSE = Token(TokenType.Kwd, "false")
val TOKEN_FOR = Token(TokenType.Kwd, "for")

