package com.bobbyesp.spowlo.data.respository

import com.bobbyesp.spowlo.data.remote.xManagerAPI
import com.bobbyesp.spowlo.domain.spotify.model.APIResponse
import com.bobbyesp.spowlo.domain.spotify.repository.APIRepository
import com.bobbyesp.spowlo.util.api.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class APIRepositoryImpl(
    private val api: xManagerAPI
): APIRepository {
    override suspend fun getApiResponse(): Flow<Resource<APIResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getAPIInfo()
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