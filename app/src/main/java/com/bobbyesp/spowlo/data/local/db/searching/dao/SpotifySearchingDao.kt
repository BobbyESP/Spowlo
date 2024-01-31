package com.bobbyesp.spowlo.data.local.db.searching.dao

import androidx.room.Dao
import androidx.room.Query
import com.bobbyesp.spowlo.data.local.db.searching.entity.SpotifySearchEntity
import com.bobbyesp.spowlo.utils.databases.BaseDao
import kotlinx.coroutines.flow.Flow
@Dao
interface SpotifySearchingDao: BaseDao<SpotifySearchEntity> {
    @Query("SELECT * FROM SpotifySearchEntity")
    fun getAllFlow(): Flow<List<SpotifySearchEntity>>

    //delete by id
    @Query("DELETE FROM SpotifySearchEntity WHERE id = :searchId")
    suspend fun deleteById(searchId: Int)
}