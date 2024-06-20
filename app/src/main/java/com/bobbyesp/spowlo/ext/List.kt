package com.bobbyesp.spowlo.ext

import com.adamratzman.spotify.models.SimpleArtist

fun List<SimpleArtist>.formatArtistsName(): String {
    return when (size) {
        0 -> ""
        1 -> first().name ?: ""
        2 -> joinToString(" & ") { it.name ?: "" }
        else -> {
            val last = last().name ?: ""
            val allButLast = subList(0, size - 1).joinToString(", ") { it.name ?: "" }
            "$allButLast & $last"
        }
    }
}