package com.bobbyesp.spowlo.ui.ext

import com.bobbyesp.spowlo.features.lyrics_downloader.data.remote.dto.SyncedLinesResponse

fun SyncedLinesResponse.toLyricsString(): String {
    val lines = this.lines
    val syncedLyricsResponse = StringBuilder()

    for (line in lines) {
        if (line.words.isBlank()) continue
        with(syncedLyricsResponse) {
            append("[${line.timeTag}] ${line.words}")
            //if the line is not the last one, append a new line, else do nothing
            if (line != lines.last()) append("\n")
        }
    }

    return syncedLyricsResponse.toString()
}