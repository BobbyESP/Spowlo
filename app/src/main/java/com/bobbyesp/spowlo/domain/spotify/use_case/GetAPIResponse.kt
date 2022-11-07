package com.bobbyesp.spowlo.domain.spotify.use_case

import com.bobbyesp.spowlo.domain.spotify.model.APIResponse
import com.bobbyesp.spowlo.domain.spotify.repository.APIRepository
import com.bobbyesp.spowlo.util.api.Resource
import kotlinx.coroutines.flow.Flow

class GetAPIResponse(
    private val repository: APIRepository
) {
    suspend operator fun invoke(): Flow<Resource<APIResponse>> {
        return repository.getApiResponse()
    }
}