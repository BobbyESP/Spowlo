package com.bobbyesp.spowlo.features.lyrics_downloader.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.db.dao.LyricsDao
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.db.entity.LyricsEntity
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.db.entity.LyricsEntityConverters

@Database(entities = [LyricsEntity::class], version = 1)
@TypeConverters(LyricsEntityConverters::class)
abstract class LyricsDatabase: RoomDatabase() {
    abstract fun lyricsDao(): LyricsDao
}