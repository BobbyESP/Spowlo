package com.bobbyesp.spowlo.features.spotifyApi.utils

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import com.adamratzman.spotify.models.Album
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.AudioFeatures
import com.adamratzman.spotify.models.Playlist
import com.adamratzman.spotify.models.Track
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@ExperimentalSerializationApi
object AudioFeaturesSaver : Saver<AudioFeatures?, String> {
    override fun restore(value: String): AudioFeatures? {
        return Json.decodeFromString(value)
    }

    override fun SaverScope.save(value: AudioFeatures?): String {
        return Json.encodeToString(value)
    }
}

@ExperimentalSerializationApi
object TrackSaver : Saver<Track, String> {
    override fun restore(value: String): Track {
        return Json.decodeFromString(value)
    }

    override fun SaverScope.save(value: Track): String {
        return Json.encodeToString(value)
    }
}

@ExperimentalSerializationApi
object AlbumSaver : Saver<Album, String> {
    override fun restore(value: String): Album {
        return Json.decodeFromString(value)
    }

    override fun SaverScope.save(value: Album): String {
        return Json.encodeToString(value)
    }
}

@ExperimentalSerializationApi
object PlaylistSaver : Saver<Playlist, String> {
    override fun restore(value: String): Playlist {
        return Json.decodeFromString(value)
    }

    override fun SaverScope.save(value: Playlist): String {
        return Json.encodeToString(value)
    }
}

@ExperimentalSerializationApi
object ArtistSaver : Saver<Artist, String> {
    override fun restore(value: String): Artist {
        return Json.decodeFromString(value)
    }

    override fun SaverScope.save(value: Artist): String {
        return Json.encodeToString(value)
    }
}