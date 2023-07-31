package com.bobbyesp.spowlo.data.local.db.music

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bobbyesp.spowlo.data.local.db.music.dao.TrackDao
import com.bobbyesp.spowlo.data.local.db.music.entity.TrackEntity
import com.bobbyesp.spowlo.data.local.db.music.entity.TrackEntityConverters

@Database(entities = [TrackEntity::class], version = 1)
@TypeConverters(TrackEntityConverters::class)
abstract class MusicAppDatabase: RoomDatabase() {
    abstract fun trackDao(): TrackDao
}