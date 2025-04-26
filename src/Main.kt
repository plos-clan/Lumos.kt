import lumos.Env
import lumos.helper.l10n
import lumos.helper.loadLocalization
import lumos.helper.setLocalLang
import lumos.logger.defaultLogger
import lumos.parser.Parser
import lumos.parser.parse
import java.io.File
import java.util.*

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
    val file = env.openFile(fileName) ?: throw Exception(l10n("error.file-not-found", fileName))
    val parser = Parser(file)
    parser.parse().dump()
    defaultLogger.check(true)

    //    env.parseFile("example/helloworld.lm")

    // val context = LLVMContextCreate()
    // val module = LLVMModuleCreateWithName("my_module")
    // val builder = LLVMCreateBuilderInContext(context)
    //
    // LLVMFunctionType(LLVMVoidType(), LLVMVoidType(), 0, 0).let { funcType ->
    //     LLVMAddFunction(module, "main", funcType).let { mainFunc ->
    //         LLVMSetFunctionCallConv(mainFunc, LLVMCCallConv)
    //         LLVMSetLinkage(mainFunc, LLVMExternalLinkage)
    //         val entry = LLVMAppendBasicBlock(mainFunc, "entry")
    //         LLVMPositionBuilderAtEnd(builder, entry)
    //         val helloWorld = LLVMBuildGlobalStringPtr(builder, "Hello, World!\n", "helloWorld")
    //         val format = LLVMBuildGlobalStringPtr(builder, "%s", "format")
    //         val printf = LLVMGetNamedFunction(module, "printf")
    //         LLVMBuildCall(builder, printf, arrayOf(format, helloWorld, null), 2, "printfCall")
    //         LLVMBuildRetVoid(builder)
    //     }
    // }
}
