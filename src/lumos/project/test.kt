package lumos.project

import lumos.logger.defaultLogger
import org.yaml.snakeyaml.Yaml

fun test() {
    try {
        val config = ProjectConfig("", Yaml().load("[aaa, bbb, ccc]"))
        println(config)
    } catch (e: Exception) {
        e.printStackTrace()
        defaultLogger.fatal("项目配置文件解析失败")
    }
}
