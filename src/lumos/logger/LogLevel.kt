package lumos.logger

enum class LogLevel {
    Debug, Info, Warning, Error, Fatal;

    override fun toString(): String {
        return when (this) {
            Debug -> "Debug"
            Info -> "Info"
            Warning -> "Warning"
            Error -> "Error"
            Fatal -> "Fatal"
        }
    }

    val fg: LogColor
        get() {
            return when (this) {
                Debug -> LogColor.Cyan
                Info -> LogColor.Green
                Warning -> LogColor.Yellow
                Error -> LogColor.Red
                Fatal -> LogColor.Red
            }
        }

    val bg: LogColor? = null
}
