package com.bobbyesp.spowlo.ui.ext

fun String.toList(): List<String> {
    val separators = listOf("/", ",")
    return split(*separators.toTypedArray<String>())
}