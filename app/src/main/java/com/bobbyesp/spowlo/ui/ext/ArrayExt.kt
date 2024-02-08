package com.bobbyesp.spowlo.ui.ext

fun Array<String>?.joinOrNullToString(): String? {
    return this?.joinToString(separator = ", ")
}