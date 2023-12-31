package com.bobbyesp.spowlo.db.dao

import androidx.room.Dao
import com.bobbyesp.spowlo.db.dao.common.BaseDao
import com.bobbyesp.spowlo.db.entity.Event

@Dao
interface EventDao: BaseDao<Event> {
    // Event queries and functions
}