package com.bobbyesp.ext

fun Array<String>?.joinOrNullToString(separator: String = ", "): String? {
    return this?.joinToString(separator = separator)
}