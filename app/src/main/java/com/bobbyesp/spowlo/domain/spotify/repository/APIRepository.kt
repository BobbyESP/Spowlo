package com.bobbyesp.spowlo.domain.spotify.repository

import com.bobbyesp.spowlo.domain.spotify.model.APIResponse

interface APIRepository {
    suspend fun getAPIInfo() : APIResponse
}