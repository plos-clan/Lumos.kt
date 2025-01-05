import lumos.Env
import lumos.logger.defaultLogger
import lumos.parser.Parser
import lumos.token.TokenType

//fun main() {
//    val context = LLVMContextCreate()
//    val module = LLVMModuleCreateWithName("my_module")
//}

const val debug = true

val env = Env()

fun main() {
    val file = env.openFile("example/helloworld.lm") ?: throw Exception("File not found")
    val parser = Parser(file)
//    while (parser.lexpeek().type != TokenType.Eof) {
//        parser.lexget()
//    }
//    defaultLogger.check(true)
    parser.parse().dump()
    defaultLogger.check(true)
//    env.parseFile("example/helloworld.lm")
}
