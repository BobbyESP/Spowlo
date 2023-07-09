package com.bobbyesp.spowlo.ui.ext

fun <T> T?.whenNotNull(block: (T) -> Unit) {
    if (this != null) block(this)
}