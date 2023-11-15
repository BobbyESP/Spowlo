package com.bobbyesp.piped_lib.data.remote

import com.bobbyesp.piped_lib.domain.model.stream.Stream
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface PipedAPI {
    suspend fun getStream(videoId: String): Stream

    companion object {
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
        fun create(engine: HttpClientEngineFactory<*> = Android): PipedAPI = PipedAPIImpl(
            HttpClient(engine) {
                install(Logging) { // Install default logger for Ktor
                    level = LogLevel.ALL
                }
                install(ContentNegotiation) { // Install JSON serializer using Kotlinx serialization
                    json(
                        contentType = ContentType.Application.Json,
                        json = json
                    )
                }
                install(HttpCache) // Install default cache (in-memory)
            }
        )
    }
}