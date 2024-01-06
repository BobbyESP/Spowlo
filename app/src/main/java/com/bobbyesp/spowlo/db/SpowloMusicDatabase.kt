package com.bobbyesp.spowlo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    version = 1,
    exportSchema = true,
    autoMigrations = []
)
@TypeConverters(DbConverters::class)
abstract class SpowloMusicDatabase : RoomDatabase() {
    companion object {
        const val DB_NAME = "spowlo_general_database.db"
    }
}