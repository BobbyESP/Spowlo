package com.bobbyesp.spowlo.db.entity.song

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.bobbyesp.spowlo.db.entity.album.AlbumEntity

@Entity(
    tableName = "song_album_map",
    primaryKeys = ["songId", "albumId"],
    foreignKeys = [
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AlbumEntity::class,
            parentColumns = ["id"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SongAlbumMap(
    @ColumnInfo(index = true) val songId: String,
    @ColumnInfo(index = true) val albumId: String,
    val index: Int,
)
