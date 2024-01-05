package com.bobbyesp.spowlo.db.dao

import androidx.room.Dao
import androidx.room.Transaction
import com.bobbyesp.spowlo.db.dao.common.BaseDao
import com.bobbyesp.spowlo.db.entity.Album
import com.bobbyesp.spowlo.db.entity.album.AlbumArtistMap
import com.bobbyesp.spowlo.db.entity.album.AlbumEntity
import com.bobbyesp.spowlo.db.entity.artist.ArtistEntity
import com.bobbyesp.spowlo.db.entity.song.SongAlbumMap
import com.zionhuang.innertube.models.SongItem
import com.zionhuang.innertube.pages.AlbumPage

@Dao
interface AlbumDao : BaseDao<Album> {

}