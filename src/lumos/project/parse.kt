package lumos.project

import lumos.logger.defaultLogger

fun ProjectConfig.parseAuthor(yaml: Any) {
    if (author.size != 0) defaultLogger.warning("项目配置文件中重复设置 author")
    if (yaml is String) {
        author.add(Author(yaml))
        return
    } else if (yaml is List<*>) {
        yaml.forEach {
            if (it is String) {
                author.add(Author(it))
            } else {
                defaultLogger.warning("项目配置文件中 author 的值不合法")
            }
        }
        return
    } else {
        defaultLogger.warning("项目配置文件中 author 的值不合法")
    }
}
