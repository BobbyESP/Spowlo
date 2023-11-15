package com.bobbyesp.piped_lib

import com.bobbyesp.piped_lib.data.remote.PipedAPI
import com.bobbyesp.piped_lib.data.remote.PipedAPIImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import org.junit.Test

class KtorClientTest {
    @Test
    fun testStreamsDataFetch() = runBlocking {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respondOk("""{}""") //<- Complete test data here
                }
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) { // Install JSON serializer using Kotlinx serialization
                json(
                    contentType = ContentType.Application.Json,
                    json = PipedAPI.json
                )
            }

        }

        val apiClientImpl = PipedAPIImpl(
            client = client
        )

        val stream = apiClientImpl.getStream("JQORMjyFhBg") //<-- Yesterday by Alan Walker (Walkerworld album)


    }

}