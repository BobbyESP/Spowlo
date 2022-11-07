package com.bobbyesp.spowlo.domain.spotify.repository
import com.bobbyesp.spowlo.domain.spotify.model.APIResponse
import com.bobbyesp.spowlo.util.api.Resource
import kotlinx.coroutines.flow.Flow

interface APIRepository {
    suspend fun getApiResponse() : Flow<Resource<APIResponse>>
}