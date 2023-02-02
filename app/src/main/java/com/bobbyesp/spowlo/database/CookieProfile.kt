package com.bobbyesp.spowlo.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class CookieProfile(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val url: String,
    val content: String
)