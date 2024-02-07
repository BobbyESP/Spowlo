package com.bobbyesp.spowlo.utils.lyrics

import android.content.Context
import android.util.Log
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.data.local.model.SelectedSong
import com.bobbyesp.spowlo.features.lyrics_downloader.data.remote.dto.Line
import com.bobbyesp.spowlo.features.lyrics_downloader.data.remote.dto.SyncedLinesResponse
import com.bobbyesp.spowlo.utils.notifications.ToastUtil
import com.kyant.tag.Metadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    suspend fun embedLyricsFile(context: Context, selectedSong: SelectedSong, lyrics: String) {
        withContext(Dispatchers.IO) {
            try {
                Log.i(
                    "SelectedSongLyricsPageViewModel",
                    "Embedding lyrics to file with path: ${selectedSong.localSongPath}"
                )
                selectedSong.localSongPath?.let { songPath ->
                    Metadata.saveLyrics(
                        songPath,
                        lyrics
                    )
                }
                ToastUtil.makeToastSuspend(
                    context,
                    context.getString(R.string.lyrics_embedded_success)
                )
            } catch (e: Exception) {
                Log.e(
                    "SelectedSongLyricsPageViewModel",
                    "Error while trying to embed lyrics: ${e.message}"
                )
                ToastUtil.makeToastSuspend(
                    context,
                    context.getString(R.string.lyrics_embedded_error)
                )
            }
        }
    }

    fun SyncedLinesResponse.toLyricsString(): String {
        val lines = this.lines
        val syncedLyricsResponse = StringBuilder()

        for (i in lines.indices) {
            val line = lines[i]
            if (line.words.isBlank()) continue
            with(syncedLyricsResponse) {
                append("[${line.timeTag}] ${line.words}")
                //if the next line is not blank, append a new line, else do nothing
                if (i < lines.size - 1 && lines[i + 1].words.isNotBlank()) append("\n")
            }
        }

        return syncedLyricsResponse.toString()
    }

    // Function to parse the time in milliseconds from a .lrc line
    fun parseTimeFromLine(line: String): Long {
        //Parse the time from line, for example: "[00:12.34]"
        val timeString = line.substring(1, line.indexOf(']'))
        val (minutes, seconds, milliseconds) = timeString.split(":").map { it.toInt() }
        return (minutes * 60 + seconds) * 1000L + milliseconds
    }
}