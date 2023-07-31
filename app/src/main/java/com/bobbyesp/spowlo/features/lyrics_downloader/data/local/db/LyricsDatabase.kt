package com.bobbyesp.spowlo.features.lyrics_downloader.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.db.dao.LyricsDao
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.db.entity.LyricsEntity

@Database(entities = [LyricsEntity::class], version = 1)
abstract class LyricsDatabase: RoomDatabase() {
    abstract fun lyricsDao(): LyricsDao
}