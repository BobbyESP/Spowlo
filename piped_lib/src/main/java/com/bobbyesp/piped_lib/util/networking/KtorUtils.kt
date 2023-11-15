package com.bobbyesp.piped_lib.util.networking

import com.bobbyesp.piped_lib.data.PipedHttpRoutes
import com.bobbyesp.piped_lib.data.remote.PipedAPI.Companion.json
import com.bobbyesp.piped_lib.util.exceptions.PipedAPIException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

suspend inline fun <reified T> makeApiCall(
    client: HttpClient,
    apiUrl: String = PipedHttpRoutes.BASE_URL,
    params: Map<String, String>
): T {
    val response: String = client.get(apiUrl) {
        url {
            params.forEach { (key, value) ->
                parameters.append(key, value)
            }
        }
    }.body()

    return try {
        json.decodeFromString<T>(response)
    } catch (e: Exception) {
        throw PipedAPIException(
            null,
            "Error parsing response: $response",
            e.message ?: "Unknown error"
        )
    }
}