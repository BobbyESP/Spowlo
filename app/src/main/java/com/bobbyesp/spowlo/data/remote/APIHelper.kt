package com.bobbyesp.spowlo.data.remote

import com.bobbyesp.spowlo.domain.spotify.model.APIResponse

interface APIHelper {
    suspend fun getAPIInfo(): APIResponse
}