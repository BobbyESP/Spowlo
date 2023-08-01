package com.bobbyesp.spowlo.utils.databases

import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.db.LyricsDatabase
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.db.entity.LyricsEntity
import javax.inject.Inject

class LyricsDbHelper @Inject constructor(
    lyricsDb: LyricsDatabase
) {
    val lyricsDao = lyricsDb.lyricsDao()

    suspend fun getByUrl(url: String) = lyricsDao.getByUrl(url)

    suspend fun insert(lyrics: LyricsEntity) = lyricsDao.insert(lyrics)
}