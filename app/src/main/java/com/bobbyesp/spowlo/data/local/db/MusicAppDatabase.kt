package com.bobbyesp.spowlo.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bobbyesp.spowlo.data.local.db.dao.TrackDao
import com.bobbyesp.spowlo.data.local.db.entity.Converters
import com.bobbyesp.spowlo.data.local.db.entity.TrackEntity

@Database(entities = [TrackEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class MusicAppDatabase: RoomDatabase() {
    abstract fun trackDao(): TrackDao
}