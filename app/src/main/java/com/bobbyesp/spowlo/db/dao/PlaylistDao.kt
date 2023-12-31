package com.bobbyesp.spowlo.db.dao

import androidx.room.Dao
import com.bobbyesp.spowlo.db.dao.common.BaseDao
import com.bobbyesp.spowlo.db.entity.Playlist

@Dao
interface PlaylistDao : BaseDao<Playlist> {
    // Playlist queries and functions
}