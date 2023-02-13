package com.bobbyesp.spowlo.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CommandShortcut::class, CommandTemplate::class, CookieProfile::class, DownloadedSongInfo::class],
    version = 1,
    autoMigrations = [
    ]
    //INFO: If changed some entities, add autoMigrations
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songsInfoDao(): SongsInfoDao
}