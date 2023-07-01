package com.bobbyesp.spowlo.data.local.db.searching.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val search : String,
    val spotifySearch: Boolean,
    val date : Long
)
