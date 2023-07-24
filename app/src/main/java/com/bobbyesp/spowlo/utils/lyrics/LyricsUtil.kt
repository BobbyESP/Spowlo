package com.bobbyesp.spowlo.utils.lyrics

import android.content.Context
import android.util.Log
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.data.local.model.SelectedSong
import com.bobbyesp.spowlo.features.lyrics_downloader.data.remote.dto.Line
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
                Log.e("SelectedSongLyricsPageViewModel", "Error while trying to embed lyrics: ${e.message}")
                ToastUtil.makeToastSuspend(
                    context,
                    context.getString(R.string.lyrics_embedded_error)
                )
            }
        }
    }
}