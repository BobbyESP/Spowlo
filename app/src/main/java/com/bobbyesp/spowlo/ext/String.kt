package com.bobbyesp.spowlo.ext

fun String.getInitials(): String {
    val words = this.split(" ")
    return words.joinToString("") { it.first().toString() }
}