package com.bobbyesp.spowlo.features.mod_downloader.data.remote

import android.util.Log
import androidx.annotation.CheckResult
import com.bobbyesp.spowlo.features.mod_downloader.domain.model.PackagesResponseDto
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object xManagerAPI {

    private val jsonFormat = Json { ignoreUnknownKeys = true }

    private const val BASE_URL = "https://xmanagerapp.com"
    private const val ENDPOINT = "/api/public.json"

    private val client = OkHttpClient()
    const val TAG = "xManagerAPI"

    private val requestAPIResponse =
        Request.Builder()
            .url(BASE_URL + ENDPOINT)
            .build()


    @CheckResult
    private suspend fun getAPIResponse(): Result<PackagesResponseDto>{
        return suspendCoroutine {
            client.newCall(requestAPIResponse).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWith(Result.failure(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body.string()
                    val packagesResponseDto = jsonFormat.decodeFromString(PackagesResponseDto.serializer(), responseData)
                    Log.d(TAG, "onResponse: $packagesResponseDto")
                    response.body.close()
                    it.resume(Result.success(packagesResponseDto))
                }
            })
        }
    }

    suspend fun getPackagesResponseDto(): Result<PackagesResponseDto> {
        return getAPIResponse()
    }
}