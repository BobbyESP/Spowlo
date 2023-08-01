package com.bobbyesp.spowlo.features.lyrics_downloader.data.remote

import android.util.Log
import com.bobbyesp.spowlo.features.lyrics_downloader.data.HttpRoutes
import com.bobbyesp.spowlo.features.lyrics_downloader.data.remote.dto.SyncedLinesResponse
import com.bobbyesp.spowlo.ui.ext.toLyricsString
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import kotlinx.serialization.json.Json

class SpotifyLyricServiceImpl(
    private val client: HttpClient
) : SpotifyLyricService {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override suspend fun getSyncedLyrics(songUrl: String): SyncedLinesResponse {
        return try {
            val apiResponse: String = client.get(HttpRoutes.LYRICS_API_BASE) {
                url {
                    parameters.append("url", songUrl)
                    parameters.append("format", "lrc")
                }
            }.body()
            json.decodeFromString<SyncedLinesResponse>(apiResponse)
        } catch (e: Exception) {
            manageError(e)
            SyncedLinesResponse(
                error = true,
                syncType = "",
                lines = emptyList()
            )
        }
    }

    override suspend fun getSyncedLyricsAsString(songUrl: String): String {
        val response = getSyncedLyrics(songUrl)

        return response.toLyricsString()
    }

    private fun manageError(e: Exception) {
        when (e) {
            is RedirectResponseException -> {
                // 3XX - Responses
                Log.e("SpotifyLyricServiceImpl", "Error: ${e.response.status.description}")
            }

            is ClientRequestException -> {
                // 4XX - Responses
                Log.e("SpotifyLyricServiceImpl", "Error: ${e.response.status.description}")
            }

            is ServerResponseException -> {
                // 5XX - Responses
                Log.e("SpotifyLyricServiceImpl", "Error: ${e.response.status.description}")
            }

            else -> {
                Log.e("SpotifyLyricServiceImpl", "Error: ${e.message}")
            }
        }
    }
}