package com.bobbyesp.spowlo.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
@kotlinx.serialization.Serializable
data class CommandTemplate(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val template: String
)
