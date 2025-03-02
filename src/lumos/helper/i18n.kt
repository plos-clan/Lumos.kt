package lumos.helper

import java.io.File

private val localization = mutableMapOf<String, Map<String, String>>()
private var localLang: String = "en-us"
private var localMap: Map<String, String>? = null

fun loadLocalization(lang: String, path: String) {
    val map = mutableMapOf<String, String>()
    val lines = File(path).readLines()
    for (line in lines) {
        val parts = line.split("=", limit = 2)
        if (parts.size == 2) {
            map[parts[0]] = parts[1]
        }
    }
    localization[lang] = map
    if (localLang == lang) {
        localMap = map
    }
}

fun setLocalLang(lang: String) {
    localLang = lang
    localMap = localization[lang]
}

fun l10n(lang: String, key: String): String {
    return localization[lang]?.get(key) ?: key
}

fun l10nOrNull(lang: String, key: String): String? {
    return localization[lang]?.get(key)
}

fun l10n(key: String): String {
    return localMap?.get(key) ?: key
}

fun l10nOrNull(key: String): String? {
    return localMap?.get(key)
}
