package com.bobbyesp.spowlo.domain.spotify_bridge.use_case

import com.bobbyesp.spowlo.domain.spotify_bridge.model.APIResponse
import com.bobbyesp.spowlo.domain.spotify_bridge.repository.APIRepository
import com.bobbyesp.spowlo.util.api.Resource
import kotlinx.coroutines.flow.Flow

class GetAPIResponse(
    private val repository: APIRepository
) {
    suspend operator fun invoke(): Flow<Resource<APIResponse>>{
        return repository.getBridgeApiResponse()
    }
}