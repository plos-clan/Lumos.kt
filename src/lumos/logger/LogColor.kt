package lumos.logger

class LogColor(
    val r: Byte,
    val g: Byte,
    val b: Byte,
    val idx: Int = -1,
) {
    constructor(r: Int, g: Int, b: Int, idx: Int = -1) : this(r.toByte(), g.toByte(), b.toByte(), idx)
    constructor(color: LogColor) : this(color.r, color.g, color.b)
    constructor(hex: Int) : this((hex shr 16).toByte(), (hex shr 8).toByte(), hex.toByte())
    constructor(color: String) : this(when {
        color.length == 7 && color[0] == '#' -> color.substring(1).toInt(16)
        color.length == 6 && color.all { it in "0123456789abcdefABCDEF" } -> color.toInt(16)
        color in colorTable -> colorTable[color]!!.toInt(16)
        else -> throw Exception("Invalid color: $color")
    })

    fun fg(): String {
        if (idx >= 0) return if (idx >= 8) "\u001B[1;3${idx - 8}m" else "\u001B[3${idx}m"
        return "\u001B[38;2;${r.toInt()};${g.toInt()};${b.toInt()}m"
    }

    fun bg(): String {
        if (idx >= 0) return if (idx >= 8) "\u001B[1;4${idx - 8}m" else "\u001B[4${idx}m"
        return "\u001B[48;2;${r.toInt()};${g.toInt()};${b.toInt()}m"
    }

    companion object {
        val Reset = "\u001B[0m"
        val Black = byIndex(0)
        val Red = byIndex(1)
        val Green = byIndex(2)
        val Yellow = byIndex(3)
        val Blue = byIndex(4)
        val Purple = byIndex(5)
        val Cyan = byIndex(6)
        val White = byIndex(7)

        val colorTable = mapOf(
            "black" to "000000",
            "red" to "FF0000",
            "green" to "00FF00",
            "yellow" to "FFFF00",
            "blue" to "0000FF",
            "purple" to "FF00FF",
            "cyan" to "00FFFF",
            "white" to "FFFFFF",
        )

        fun byIndex(idx: Int) = LogColor(0, 0, 0, idx)
    }
}
