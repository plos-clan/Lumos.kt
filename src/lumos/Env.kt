package lumos

import lumos.ast.Root
import lumos.helper.l10n
import lumos.logger.Logger
import lumos.logger.defaultLogger
import lumos.parser.Parser
import lumos.parser.parse
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.global.LLVM.LLVMModuleCreateWithName
import java.io.File

class FileData(
    env: Env,
    val file: File, // 文件对象
    val literalPath: String = "", // 第一次打开文件时使用的路径
) {
    val relativePath = file.toRelativeString(File(".")) // 相对路径
    val path: String = file.absolutePath // 绝对路径
    val name: String = file.name // 文件名
    val data: ByteArray // 文件内容
    val text: String // 文件内容

    init {
        if (file.length() > env.settings["file-size-limit"] as Int) {
            throw Exception(l10n("error.file-size-limit").format(file.absolutePath))
        }
        data = file.readBytes()
        text = data.decodeToString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return path == (other as FileData).path
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}

class Project {}

class Env {
    val logger = defaultLogger

    // 默认搜索路径
    val PATH: MutableList<String> = mutableListOf(".")

    // 所有打开的文件
    val files: MutableMap<String, FileData> = mutableMapOf()

    // 所有解析过的模块
    val modules: MutableMap<String, Root> = mutableMapOf()

    val settings: MutableMap<String, Any> = mutableMapOf(
        "file-limit" to 1024, // 文件数量限制
        "file-size-limit" to 128 * 1024 * 1024, // 文件大小限制
        "module-limit" to 1024, // 模块数量限制
    )

    val llvmModule: LLVMModuleRef = LLVMModuleCreateWithName("main")

    fun openFile(name: String): FileData? {
        if (name.startsWith("/")) {
            val file = File(name)
            if (!file.exists()) {
                logger.warning(l10n("error.file-not-found").format(name))
                return null
            }
            if (file.absolutePath !in files) {
                if (files.size >= settings["file-limit"] as Int) {
                    throw Exception("文件数量超过限制")
                }
                files[file.absolutePath] = FileData(this, file, name)
            }
            return files[file.absolutePath]!!
        }
        for (path in PATH) {
            val file = File("$path/$name")
            if (!file.exists()) continue
            if (file.absolutePath !in files) {
                if (files.size >= settings["file-limit"] as Int) {
                    throw Exception("文件数量超过限制")
                }
                files[file.absolutePath] = FileData(this, file, name)
            }
            return files[file.absolutePath]!!
        }
        logger.warning(l10n("error.file-not-found").format(name))
        return null
    }

    fun parseFile(name: String, logger: Logger = defaultLogger): Root? {
        val file = openFile(name) ?: return null
        if (file.path in modules) return modules[file.path]!!
        val parser = Parser(file, logger)
        modules[file.path] = parser.parse()
        return modules[file.path]!!
    }
}
