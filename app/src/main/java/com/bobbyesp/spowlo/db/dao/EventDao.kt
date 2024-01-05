package com.bobbyesp.spowlo.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.bobbyesp.spowlo.db.dao.common.BaseDao
import com.bobbyesp.spowlo.db.entity.Event
import com.bobbyesp.spowlo.db.entity.event.EventWithSong
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao : BaseDao<Event> {
    @Transaction
    @Query("SELECT * FROM event ORDER BY rowId DESC")
    fun events(): Flow<List<EventWithSong>>

    @Query("DELETE FROM event")
    fun clearListenHistory()
}