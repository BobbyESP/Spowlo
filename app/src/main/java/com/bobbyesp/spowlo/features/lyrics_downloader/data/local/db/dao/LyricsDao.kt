package com.bobbyesp.spowlo.features.lyrics_downloader.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.db.entity.LyricsEntity

@Dao
interface LyricsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lyrics: List<LyricsEntity>)

    @Upsert
    suspend fun upsertAll(lyrics: List<LyricsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lyrics: LyricsEntity)

    @Upsert
    suspend fun upsert(lyrics: LyricsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lyrics: List<LyricsEntity>)

    @Query("SELECT * FROM LyricsEntity")
    suspend fun getAll(): List<LyricsEntity>

    @Query("SELECT * FROM LyricsEntity WHERE id = :lyricsId")
    suspend fun getById(lyricsId: Int): LyricsEntity?

    @Query("DELETE FROM LyricsEntity WHERE id = :lyricsId")
    suspend fun deleteById(lyricsId: Int)

    @Query("DELETE FROM LyricsEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM LyricsEntity WHERE url LIKE :url")
    suspend fun getByUrl(url: String): List<LyricsEntity>

    @Query("DELETE FROM LyricsEntity WHERE url LIKE :url")
    suspend fun deleteByUrl(url: String)

    @Query("SELECT * FROM LyricsEntity WHERE url LIKE :url AND lyricsResponse = :lyricsResponse")
    suspend fun getByUrlAndLyricsResponse(url: String, lyricsResponse: String): List<LyricsEntity>

    @Query("SELECT * FROM LyricsEntity WHERE lyricsResponse = :lyricsResponse")
    suspend fun getByLyricsResponse(lyricsResponse: String): List<LyricsEntity>


}