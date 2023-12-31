package com.bobbyesp.spowlo.db.dao

import androidx.room.Dao
import com.bobbyesp.spowlo.db.dao.common.BaseDao
import com.bobbyesp.spowlo.db.entity.Artist

@Dao
interface ArtistDao : BaseDao<Artist> {
    // Artist queries and functions
}