package com.bobbyesp.spowlo.domain.spotify_bridge.repository

import com.bobbyesp.spowlo.domain.spotify_bridge.model.APIResponse
import com.bobbyesp.spowlo.util.api.Resource
import kotlinx.coroutines.flow.Flow

interface APIRepository {
    suspend fun getBridgeApiResponse() : Flow<Resource<APIResponse>>
}