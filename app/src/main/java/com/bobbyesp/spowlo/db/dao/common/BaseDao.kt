package com.bobbyesp.spowlo.db.dao.common

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Upsert

interface BaseDao<T: Any> {
    /**
     * Insert an entity in the database.
     * If there is a conflict, replace the entity
     * @param entity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T)

    /**
     * Update an entity.
     * @param entity
     */
    @Update
    suspend fun update(entity: T)

    /**
     * Delete an entity.
     * @param entity
     */
    @Delete
    suspend fun delete(entity: T)

    /**
     * Updates an existing row if a specified value already exists in a table,
     * and insert a new row if the specified value doesn't already exist
     * @param entity
     */
    @Upsert
    suspend fun upsert(entity: T)
}