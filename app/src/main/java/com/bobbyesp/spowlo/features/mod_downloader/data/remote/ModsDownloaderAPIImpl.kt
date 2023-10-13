package com.bobbyesp.spowlo.features.mod_downloader.data.remote

import android.content.Context
import android.util.Log
import com.bobbyesp.spowlo.features.mod_downloader.domain.model.APIResponseDto
import com.bobbyesp.spowlo.utils.UpdateUtil
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ModsDownloaderAPIImpl(
    private val client: HttpClient
): ModsDownloaderAPIService {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override suspend fun getAPIResponse(): Result<APIResponseDto> {
        return try {
            val apiResponse: String = client.get("https://jsonkeeper.com/b/VGY7").body()
            json.decodeFromString<APIResponseDto>(apiResponse).let {
                Result.success(it)
            }
        } catch (e: Exception) {
            Log.e("ModsDownloaderAPIImpl", "getAPIResponse: ", e)
            Result.failure(e)
        }
    }

    override suspend fun downloadPackage(
        context: Context,
    ): Flow<UpdateUtil.DownloadStatus> {
        withContext(Dispatchers.IO) {
            TODO("Not yet implemented")
        }
//        return emptyFlow()
    }
}