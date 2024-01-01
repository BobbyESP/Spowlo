package com.bobbyesp.utilities.ext

fun <T> T?.whenNotNull(block: (T) -> Unit) {
    if (this != null) block(this)
}

fun <T> T?.orUnknown(): String {
    return this?.toString() ?: "Unknown"
}

inline fun <reified T> getClassOfType(): Class<T> {
    return T::class.java
}