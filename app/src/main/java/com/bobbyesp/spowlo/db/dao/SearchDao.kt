package com.bobbyesp.spowlo.db.dao

import androidx.room.Dao
import com.bobbyesp.spowlo.db.dao.common.BaseDao
import com.zionhuang.innertube.models.SongItem

@Dao
interface SearchDao: BaseDao<SongItem> {
    // Search queries and functions
}