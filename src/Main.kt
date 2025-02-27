import lumos.Env
import lumos.logger.defaultLogger
import lumos.parser.Parser
import lumos.util.loadLocalization
import lumos.util.setLocalLang
import java.io.File
import java.util.*

//fun main() {
//    val context = LLVMContextCreate()
//    val module = LLVMModuleCreateWithName("my_module")
//}

const val debug = true

val env = Env()

fun initLocalization() {
    val locale = Locale.getDefault()
    val lang = "${locale.language}-${locale.country.lowercase(locale)}"
    setLocalLang(lang)
    val langDir = File("lang")
    if (langDir.exists() && langDir.isDirectory) {
        langDir.listFiles()?.forEach { file ->
            if (file.isFile && file.extension == "lang") {
                loadLocalization(file.nameWithoutExtension, file.path)
            }
        }
    }
}

fun main() {
    initLocalization()
    val fileName = "example/helloworld.lm"
    val file = env.openFile(fileName) ?: throw Exception("File `${fileName}` not found")
    val parser = Parser(file)
//    while (parser.lexpeek().type != TokenType.Eof) {
//        parser.lexget()
//    }
//    defaultLogger.check(true)
    parser.parse().dump()
    defaultLogger.check(true)
//    env.parseFile("example/helloworld.lm")
}
