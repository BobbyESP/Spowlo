package com.bobbyesp.spowlo.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DownloadedSongInfo::class, CommandTemplate::class], version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songInfoDao(): SongInfoDao
}