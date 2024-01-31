package com.bobbyesp.spowlo.data.local.db.searching.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.bobbyesp.spowlo.data.local.db.searching.entity.SearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaStoreSearchingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(searches: List<SearchEntity>)

    @Upsert
    suspend fun upsertAll(searches: List<SearchEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(search: SearchEntity)

    @Upsert
    suspend fun upsert(search: SearchEntity)

    @Query("SELECT * FROM SearchEntity")
    suspend fun getAll(): List<SearchEntity>

    @Query("SELECT * FROM SearchEntity")
    fun getAllWithFlow(): Flow<List<SearchEntity>>

    @Query("SELECT * FROM SearchEntity WHERE id = :searchId")
    suspend fun getById(searchId: Int): SearchEntity?

    @Query("DELETE FROM SearchEntity WHERE id = :searchId")
    suspend fun deleteById(searchId: Int)

    @Query("DELETE FROM SearchEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM SearchEntity WHERE search LIKE :search")
    suspend fun getBySearch(search: String): List<SearchEntity>

    @Query("SELECT * FROM SearchEntity WHERE search LIKE :search AND spotifySearch = :spotifySearch")
    suspend fun getBySearchAndSpotifySearch(
        search: String,
        spotifySearch: Boolean
    ): List<SearchEntity>

    @Query("SELECT * FROM SearchEntity WHERE spotifySearch = :spotifySearch")
    suspend fun getBySpotifySearch(spotifySearch: Boolean): List<SearchEntity>

    @Query("SELECT * FROM SearchEntity WHERE date = :date")
    suspend fun getByDate(date: Long): List<SearchEntity>

    @Query("SELECT * FROM SearchEntity WHERE date >= :date")
    suspend fun getByDateAfter(date: Long): List<SearchEntity>

    @Query("SELECT * FROM SearchEntity WHERE date <= :date")
    suspend fun getByDateBefore(date: Long): List<SearchEntity>
}