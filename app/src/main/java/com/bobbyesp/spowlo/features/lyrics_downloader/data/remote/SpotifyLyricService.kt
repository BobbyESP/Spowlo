package com.bobbyesp.spowlo.features.lyrics_downloader.data.remote

import com.bobbyesp.spowlo.features.lyrics_downloader.data.remote.dto.SyncedLinesResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface SpotifyLyricService {
    suspend fun getSyncedLyrics(songUrl: String): SyncedLinesResponse

    suspend fun getSyncedLyricsAsString(songUrl: String): String

    companion object {
        fun create(): SpotifyLyricService = SpotifyLyricServiceImpl(
            HttpClient(Android) {
                install(Logging) {
                    level = LogLevel.ALL
                }
                install(ContentNegotiation) {
                    json(
                        contentType = ContentType.Application.Json,
                        json = Json {
                            ignoreUnknownKeys = true
                            encodeDefaults = true
                        }
                    )
                }
            }
        )
    }
}