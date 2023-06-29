package com.bobbyesp.spowlo.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.bobbyesp.spowlo.data.local.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<TrackEntity>)

    @Upsert
    suspend fun upsertAll(tracks: List<TrackEntity>)

    @Query("SELECT * FROM TrackEntity")
    suspend fun getAll(): List<TrackEntity>

    @Query("SELECT * FROM TrackEntity WHERE id = :trackId")
    suspend fun getById(trackId: String): TrackEntity?

    @Delete
    suspend fun delete(track: TrackEntity)

    @Query("DELETE FROM TrackEntity")
    suspend fun deleteAll()


}