package com.bobbyesp.spowlo.ext

import java.util.Locale

fun String.getInitials(): String {
    val words = this.split(" ")
    return words.joinToString("") { it.first().toString() }
}

fun String.capitalize(): String {
    return this.replaceFirstChar {
        if(it.isLowerCase()) it.titlecase(Locale.getDefault())
        else it.toString()
    }
}