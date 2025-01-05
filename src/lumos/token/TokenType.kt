package lumos.token

enum class TokenType {
    Inv, // 未知类型
    Space, // 空白符
    Comment, // 注释
    Num, // 数字
    Str, // 字符串
    Chr, // 字符
    FmtBegin, // 格式化字符串开始
    FmtData, // 格式化字符串数据
    FmtEnd, // 格式化字符串结束
    FmtExprBegin, // 格式化表达式开始
    FmtExprEnd, // 格式化表达式结束
    TemplateBegin, // 模板开始
    TemplateEnd, // 模板结束
    Op, // 运算符
    OpName, // 运算符名称
    Attr, // 属性
    Punc, // 分隔符
    RootNS, // 根命名空间
    Sym, // 标识符
    Kwd, // 关键字
    Eof, // 文件结束
    Macro, // 宏
    Type, // 类型
}
