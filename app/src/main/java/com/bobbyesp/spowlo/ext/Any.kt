package com.bobbyesp.spowlo.ext

fun Any.asQualifiedName(): String = this::class.qualifiedName.toString()