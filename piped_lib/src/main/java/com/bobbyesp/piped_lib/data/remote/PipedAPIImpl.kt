package com.bobbyesp.piped_lib.data.remote

import com.bobbyesp.piped_lib.data.PipedHttpRoutes
import com.bobbyesp.piped_lib.domain.model.stream.Stream
import com.bobbyesp.piped_lib.util.networking.makeApiCall
import io.ktor.client.HttpClient

class PipedAPIImpl(
    private val client: HttpClient
) : PipedAPI {
    override suspend fun getStream(videoId: String): Stream {
        val params = mapOf(
            "videoId" to videoId
        )
        return makeApiCall(
            client= client,
            apiUrl = PipedHttpRoutes.Streams.STREAMS,
            params = params
        )
    }
}