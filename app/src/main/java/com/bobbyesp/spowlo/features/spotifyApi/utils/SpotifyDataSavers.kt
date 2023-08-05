package com.bobbyesp.spowlo.features.spotifyApi.utils

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import com.adamratzman.spotify.models.AudioFeatures
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