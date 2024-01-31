package com.bobbyesp.spowlo.data.local.db.searching.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType

@Entity
data class SpotifySearchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val search: String,
    val date: Long = System.currentTimeMillis(),
    val type: SpotifyItemType? = null,
)
