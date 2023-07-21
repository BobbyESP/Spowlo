package com.bobbyesp.spowlo.ui.ext

fun <T> T?.whenNotNull(block: (T) -> Unit) {
    if (this != null) block(this)
}

inline fun <reified T> getClassOfType(): Class<T> {
    return T::class.java
}