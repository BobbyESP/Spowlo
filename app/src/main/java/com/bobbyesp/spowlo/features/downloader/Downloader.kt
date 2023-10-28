package com.bobbyesp.spowlo.features.downloader

object Downloader {
    fun makeKey(title: String, artist: String): String {
        return "$title-$artist"
    }
}