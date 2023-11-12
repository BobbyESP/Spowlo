package com.bobbyesp.spowlo.ui.ext

fun String.toList(): List<String> {
    val separators = listOf("/", ",")
    return split(*separators.toTypedArray<String>())
}

fun String.getNumbers(): Int {
    return replace("[^0-9]".toRegex(), "").toInt()
}

fun String.getId(): String {
    return split(":").last()
}
fun String.containsEllipsis(): Boolean {
    return this.contains("…")
}

fun String.clearOutputWithEllipsis(): String {
    return this.split("\n")
        .filterNot { it.contains("…") }
        .joinToString("\n")
}

private val domainRegex = Regex("""http(s)?://(\w*(www|m|account|sso))?|/.*""")
fun String.toDomain(): String {
    return this.replace(domainRegex, "")
}