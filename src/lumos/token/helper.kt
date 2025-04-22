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
val TOKEN_ASSIGN = Token(TokenType.Punc, "=")
val TOKEN_EQUAL = Token(TokenType.Punc, "==")
val TOKEN_NOT_EQUAL = Token(TokenType.Punc, "!=")

// 关键字
val KWD_CLASS = Token(TokenType.Kwd, "class")
val KWD_TRUE = Token(TokenType.Kwd, "true")
val KWD_FALSE = Token(TokenType.Kwd, "false")
val KWD_FOR = Token(TokenType.Kwd, "for")
val KWD_FUNC = Token(TokenType.Kwd, "fn")
val KWD_IF = Token(TokenType.Kwd, "if")
val KWD_ELIF = Token(TokenType.Kwd, "elif")
val KWD_ELSE = Token(TokenType.Kwd, "else")
val KWD_IN = Token(TokenType.Kwd, "in")
val KWD_VAL = Token(TokenType.Kwd, "val")
val KWD_VAR = Token(TokenType.Kwd, "var")
val KWD_BY = Token(TokenType.Kwd, "by")
val KWD_AS = Token(TokenType.Kwd, "as")
