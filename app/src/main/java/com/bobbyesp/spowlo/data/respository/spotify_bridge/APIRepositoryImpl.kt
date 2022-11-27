package com.bobbyesp.spowlo.data.respository.spotify_bridge

import com.bobbyesp.spowlo.data.remote.spotify_bridge.SpotifyBridgeAPI
import com.bobbyesp.spowlo.domain.spotify_bridge.model.APIResponse
import com.bobbyesp.spowlo.domain.spotify_bridge.repository.APIRepository
import com.bobbyesp.spowlo.util.api.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class APIRepositoryImpl(
    private val api: SpotifyBridgeAPI
): APIRepository {
    override suspend fun getBridgeApiResponse(): Flow<Resource<APIResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getYTApiResponse()
            if(response.isSuccessful) {
                response.body()?.let { resultResponse ->
                    emit(Resource.Success(resultResponse))
                }
            } else {
                emit(Resource.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occured"))
        }
    }
}