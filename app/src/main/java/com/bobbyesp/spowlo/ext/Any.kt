package com.bobbyesp.spowlo.ext

fun Any.formatAsClassToRoute(): String = this::class.qualifiedName.toString()