package com.bobbyesp.spowlo.data.remote

import com.bobbyesp.spowlo.domain.spotify.model.APIResponse
import retrofit2.Response
import retrofit2.http.GET

interface xManagerAPI {

    @GET("/api/public.json")
    suspend fun getAPIInfo(): Response<APIResponse>

    companion object {
        const val BASE_URL = "https://xmanagerapp.com/"
    }

}