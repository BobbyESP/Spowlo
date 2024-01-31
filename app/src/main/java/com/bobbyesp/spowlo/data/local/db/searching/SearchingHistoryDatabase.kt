package com.bobbyesp.spowlo.data.local.db.searching

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.bobbyesp.spowlo.data.local.db.searching.dao.MediaStoreSearchingDao
import com.bobbyesp.spowlo.data.local.db.searching.dao.SpotifySearchingDao
import com.bobbyesp.spowlo.data.local.db.searching.entity.SearchEntity
import com.bobbyesp.spowlo.data.local.db.searching.entity.SpotifySearchEntity

@Database(
    entities = [SearchEntity::class, SpotifySearchEntity::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class SearchingHistoryDatabase : RoomDatabase() {
    abstract fun searchingDao(): MediaStoreSearchingDao
    abstract fun spotifySearchingDao(): SpotifySearchingDao
}