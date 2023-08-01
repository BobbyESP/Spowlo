package com.bobbyesp.spowlo.data.local.db.searching

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bobbyesp.spowlo.data.local.db.searching.dao.SearchingDao
import com.bobbyesp.spowlo.data.local.db.searching.entity.SearchEntity

@Database(entities = [SearchEntity::class], version = 1)
abstract class SearchingHistoryDatabase : RoomDatabase() {
    abstract fun searchingDao(): SearchingDao
}