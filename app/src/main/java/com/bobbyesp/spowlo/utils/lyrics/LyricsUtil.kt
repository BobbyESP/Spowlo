package com.bobbyesp.spowlo.utils.lyrics

import com.bobbyesp.spowlo.features.lyrics_downloader.data.remote.dto.Line

object LyricsUtil {
    fun parseLyrics(lyrics: String): List<Line> {
        val lines = lyrics.split("\n")
        val lyricsLines = mutableListOf<Line>()
        for (line in lines) {
            val timeTag = line.substring(1, 9)
            val words = line.substring(10)
            lyricsLines.add(Line(timeTag, words))
        }
        return lyricsLines
    }
}