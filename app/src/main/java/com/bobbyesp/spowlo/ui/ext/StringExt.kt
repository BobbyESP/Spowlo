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