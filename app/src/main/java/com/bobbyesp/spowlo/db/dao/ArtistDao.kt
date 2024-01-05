package com.bobbyesp.spowlo.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.bobbyesp.spowlo.db.dao.common.BaseDao
import com.bobbyesp.spowlo.db.entity.Artist
import com.bobbyesp.spowlo.db.entity.artist.ArtistEntity

@Dao
interface ArtistDao : BaseDao<Artist> {
    @Query("SELECT * FROM artist WHERE name = :name")
    fun artistByName(name: String): ArtistEntity?
}