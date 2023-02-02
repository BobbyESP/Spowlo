package com.bobbyesp.spowlo.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class CommandShortcut(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val option: String
)