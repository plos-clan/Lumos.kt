package lumos.token

import lumos.helper.encodeString
import lumos.lexer.LexerPos
import lumos.logger.internalError
import lumos.token.TokenType.*

data class Token(
    val type: TokenType, // token 类型
    val raw: String, // 原始文本
    val pos: TokenPos, // 起始位置
    val endPos: TokenPos, // 结束位置
    val data: TokenData, // token 数据
) {
    val text get() = data.text // 解析后的文本

    constructor(type: TokenType, raw: String, pos: TokenPos, endPos: TokenPos) : this(
        type, raw, pos, endPos,
        when (type) {
            Inv -> emptyTokenData
            Space -> SpaceData(raw)
            Comment -> CommentData(raw)
            Sym -> SymData(raw, false)
            Num -> NumData(raw)
            Kwd -> TokenData()
            TemplateBegin -> TokenData()
            TemplateEnd -> TokenData()
            Op -> OpData(raw)
            OpName -> OpNameData(raw)
            Str -> StrData(raw)
            Chr -> StrData(raw)
            FmtBegin -> TokenData()
            FmtData -> FmtData(raw)
            FmtEnd -> TokenData()
            Attr -> TokenData()
            Punc -> TokenData()
            Eof -> TokenData()
            RootNS -> TokenData()
            FmtExprBegin -> TokenData()
            FmtExprEnd -> TokenData()
            Macro -> TokenData()
            Type -> internalError("Type 类型的 token 不应该使用此函数初始化", pos)
        },
    )

    constructor(type: TokenType, raw: String, pos: LexerPos, endPos: LexerPos, data: TokenData) : this(
        type, raw, TokenPos(pos), TokenPos(endPos), data,
    )

    constructor(type: TokenType, raw: String, pos: LexerPos, endPos: LexerPos) : this(
        type, raw, TokenPos(pos), TokenPos(endPos),
    )

    constructor(type: TokenType, raw: String) : this(type, raw, invalidTokenPos, invalidTokenPos)

    init {
        if (type != Inv && type != Eof) {
            raw.isEmpty() && internalError("empty token")
            pos == endPos && pos != invalidTokenPos && internalError("empty token")
        }
    }

    override fun toString(): String {
        return "$type ${data.encode() ?: "'${encodeString(raw)}'"}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Token) return false
        return type == other.type && raw == other.raw
    }

    override fun hashCode(): Int {
        return 31 * type.hashCode() + raw.hashCode()
    }
}
