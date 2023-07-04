package com.bobbyesp.spowlo.data.local.db.music.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.bobbyesp.spowlo.data.local.db.music.entity.TrackEntity

@Dao
interface TrackDao {
    /**
     * Insert a list of tracks
     * If there is a conflict, replace the track
     * @param tracks
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<TrackEntity>)

    /**
     * Update a list of tracks
     * @param tracks
     */
    @Upsert
    suspend fun upsertAll(tracks: List<TrackEntity>)

    @Query("SELECT * FROM TrackEntity")
    fun pagingSource(): PagingSource<Int, TrackEntity>

    /**
     * Insert a track
     * @param track
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: TrackEntity)


    /**
     * Update a track
     * @param track
     */
    @Upsert
    suspend fun upsert(track: TrackEntity)

    /**
     * Get all tracks from the database
     * @return [List] of [TrackEntity]
     */
    @Query("SELECT * FROM TrackEntity")
    suspend fun getAll(): List<TrackEntity>

    /**
     * Get a track by id
     * @param trackId
     * @return [TrackEntity] with the given id (if exists)
     */
    @Query("SELECT * FROM TrackEntity WHERE id = :trackId")
    suspend fun getById(trackId: String): TrackEntity?

    /**
     * Delete a track
     * @param track
     */
    @Delete
    suspend fun delete(track: TrackEntity)

    /**
     * Delete a track by id
     * @param trackId
     */
    @Query("DELETE FROM TrackEntity WHERE id = :trackId")
    suspend fun deleteById(trackId: String)

    /**
     * Delete all tracks
     */
    @Query("DELETE FROM TrackEntity")
    suspend fun deleteAll()


}