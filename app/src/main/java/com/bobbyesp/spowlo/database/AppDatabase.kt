package com.bobbyesp.spowlo.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CommandShortcut::class, CommandTemplate::class, CookieProfile::class, DownloadedSongInfo::class],
    version = 5,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
    ]
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun songsInfoDao(): SongsInfoDao
}