package com.bobbyesp.appmodules.core.ext

import com.bobbyesp.appmodules.core.utils.SpotifyUtils
import com.spotify.metadata.Metadata

val Metadata.Track.imageUrl: String? get() = SpotifyUtils.getImageUrl(this.album.coverGroup.imageList.first { it.size == Metadata.Image.Size.DEFAULT }.fileId)