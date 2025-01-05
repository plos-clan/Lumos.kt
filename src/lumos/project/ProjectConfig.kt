package lumos.project

import debug
import lumos.logger.defaultLogger
import lumos.logger.internalError
import org.yaml.snakeyaml.Yaml

class ProjectConfig(
    val path: String,
    yaml: Any,
) {
//    val name get() = path.substringAfterLast('/').substringBeforeLast('.')

    init {
        yaml as Map<String, Any>
        println(yaml)
    }
}


fun test() {
    try {
        val config = ProjectConfig("", Yaml().load("[aaa, bbb, ccc]"))
        println(config)
    } catch (e: Exception) {
        e.printStackTrace()
        defaultLogger.fatal("项目配置文件解析失败")
    }
}
