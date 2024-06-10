package com.bobbyesp.ui.ext

internal fun String.getInitials(): String {
    val words = this.split(" ").take(2)
    return words.joinToString("") { it.first().toString() }
}