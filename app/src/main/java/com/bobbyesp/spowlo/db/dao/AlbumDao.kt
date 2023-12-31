package com.bobbyesp.spowlo.db.dao

import androidx.room.Dao
import com.bobbyesp.spowlo.db.dao.common.BaseDao
import com.bobbyesp.spowlo.db.entity.Album

@Dao
interface AlbumDao : BaseDao<Album> {
    // Album queries and functions
}