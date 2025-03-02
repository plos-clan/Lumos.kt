package lumos.helper

fun encodeURI(uri: String): String {
    val sb = StringBuilder()
    for (c in uri) {
        if (c in 'a'..'z' || c in 'A'..'Z' || c in '0'..'9' || c == '-' || c == '_' || c == '.' || c == '~') {
            sb.append(c)
        } else {
            sb.append("%")
            sb.append(c.code.toString(16).uppercase())
        }
    }
    return sb.toString()
}

fun decodeURI(uri: String): String {
    val sb = StringBuilder()
    var i = -1
    while (++i < uri.length) {
        if (uri[i] == '+') {
            sb.append(' ')
            continue
        }
        if (uri[i] != '%') {
            sb.append(uri[i])
            continue
        }
        if (uri.length >= i + 3 && uri.substring(i + 1, i + 3).all { it in "0123456789ABCDEFabcdef" }) {
            sb.append(uri.substring(i + 1, i + 3).toInt(16).toChar())
            i += 2
        } else {
            sb.append(uri[i])
        }
    }
    return sb.toString()
}

fun encodeString(str: String): String {
    val sb = StringBuilder()
    for (c in str) {
        when (c) {
            '\u0007' -> sb.append("\\a")
            '\u0008' -> sb.append("\\b")
            '\u001B' -> sb.append("\\e")
            '\u000C' -> sb.append("\\f")
            '\u000A' -> sb.append("\\n")
            '\u000D' -> sb.append("\\r")
            '\u0009' -> sb.append("\\t")
            '\u000B' -> sb.append("\\v")
            '\\' -> sb.append("\\\\")
            '\"' -> sb.append("\\\"")
            '\'' -> sb.append("\\'")
            '`' -> sb.append("\\`")
            in '\u0000' .. '\u001f' -> {
                sb.append("\\x")
                sb.append(c.code.toString(16).padStart(2, '0'))
            }
            else -> sb.append(c)
        }
    }
    return sb.toString()
}

fun decodeString(str: String): String {
    val sb = StringBuilder()
    var i = -1
    while (++i < str.length) {
        if (str[i] != '\\') {
            sb.append(str[i])
            continue
        }
        if (++i == str.length) throw Exception("反斜杠后面没有字符")
        when (str[i].lowercaseChar()) {
            'a' -> sb.append('\u0007')
            'b' -> sb.append('\u0008')
            'e' -> sb.append('\u001B')
            'f' -> sb.append('\u000C')
            'n' -> sb.append('\u000A')
            'r' -> sb.append('\u000D')
            't' -> sb.append('\u0009')
            'v' -> sb.append('\u000B')

            in '0'..'9' -> {
                var j = i
                while (j < i + 3 && str[j] in '0'..'9') j++
                var code = str.substring(i, j).toInt()
                if (code > 255) {
                    j--
                    code = str.substring(i, j).toInt()
                }
                sb.append(Character.toChars(code))
                i = j - 1
            }

            'x' -> {
                i++
                when {
                    str.length >= i + 2 && str.substring(i, i + 2).all { it in "0123456789ABCDEFabcdef" } -> {
                        sb.append(Character.toChars(str.substring(i, i + 2).toInt(16)))
                        i += 2 - 1

                    }

                    else -> throw Exception("无效的十六进制转义")
                }
            }

            'u' -> {
                i++
                when {
                    str.length >= i + 6 && str.substring(i, i + 6).all { it in "0123456789ABCDEFabcdef" } -> {
                        sb.append(Character.toChars(str.substring(i, i + 6).toInt(16)))
                        i += 6 - 1
                    }

                    str.length >= i + 4 && str.substring(i, i + 4).all { it in "0123456789ABCDEFabcdef" } -> {
                        sb.append(Character.toChars(str.substring(i, i + 4).toInt(16)))
                        i += 4 - 1
                    }

                    else -> throw Exception("无效的 Unicode 转义")
                }
            }

            else -> sb.append(str[i])
        }
    }
    return sb.toString()
}
