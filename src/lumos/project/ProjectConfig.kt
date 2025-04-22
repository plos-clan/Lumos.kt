package lumos.project

import lumos.logger.Logger
import lumos.logger.defaultLogger
import org.yaml.snakeyaml.Yaml
import java.io.File

open class Author(
    val name: String, // 姓名
    val email: String? = null, // 邮箱
    val url: String? = null, // 个人主页
)

class Team(
    name: String,
    email: String? = null,
    url: String? = null,
) : Author(name, email, url) {
    val members: MutableList<Author> = mutableListOf()
}

class ProjectConfig(
    val path: String,
    yaml: Any,
) {
    var author: MutableList<Author> = mutableListOf()
    var email: MutableList<String> = mutableListOf()
//    val name get() = path.substringAfterLast('/').substringBeforeLast('.')

    fun check(logger: Logger) {
        if (author == null) logger.warning("项目配置文件中未设置 author")
        if (email == null) logger.warning("项目配置文件中未设置 email")
    }

    init {
        yaml as Map<String, Any>
        println(yaml)
    }
}

// 加载项目配置文件
fun loadProject(path: String): ProjectConfig {
    if (path.isEmpty()) defaultLogger.fatal("项目配置文件路径为空")
    val file = File("$path/lumos.yaml")
    if (!file.exists()) defaultLogger.fatal("项目配置文件不存在")
    val config = ProjectConfig(path, Yaml().load(file.readText()))
    return config
}
