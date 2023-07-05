package com.bobbyesp.spowlo.ui.ext

fun Long.toDate(): String {
    return java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(java.util.Date(this))
}