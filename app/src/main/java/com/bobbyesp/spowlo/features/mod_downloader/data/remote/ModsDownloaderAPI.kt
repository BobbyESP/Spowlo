package com.bobbyesp.spowlo.features.mod_downloader.data.remote

import android.content.Context
import androidx.annotation.CheckResult
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.features.mod_downloader.domain.model.refactor.APIResponseDto
import com.bobbyesp.spowlo.features.mod_downloader.domain.model.refactor.ApkResponseDto
import com.bobbyesp.spowlo.utils.UpdateUtil
import com.bobbyesp.spowlo.utils.UpdateUtil.downloadFileWithProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ModsDownloaderAPI {

    private val jsonFormat = Json { ignoreUnknownKeys = true }

    private const val BASE_URL = "https://raw.githubusercontent.com/"
    private const val ENDPOINT = "BobbyESP/Spowlo/main/API_Spowlo_APKs.json"

    private val client = OkHttpClient()
    const val TAG = "APKsDownloaderAPI"

    private val requestAPIResponse =
        Request.Builder()
            .url(BASE_URL + ENDPOINT)
            .build()


    @CheckResult
    private suspend fun getAPIResponse(): Result<APIResponseDto>{
        return suspendCoroutine {
            client.newCall(requestAPIResponse).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWith(Result.failure(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body.string()
                    val apiResponse = jsonFormat.decodeFromString(APIResponseDto.serializer(), responseData)
                    response.body.close()
                    it.resume(Result.success(apiResponse))
                }
            })
        }
    }

    suspend fun callModsAPI(): Result<APIResponseDto> {
        return getAPIResponse()
    }

    private fun Context.getSpotifyAPK() =
        File(getExternalFilesDir("apk"), "Spotify_Spowlo_Mod.apk")

    suspend fun downloadPackage(
        context: Context = App.context,
        apiResponseDto: APIResponseDto,
        listName: String,
        index: Int
        ): Flow<UpdateUtil.DownloadStatus> {
        withContext(Dispatchers.IO){
            var selectedList = emptyList<ApkResponseDto>()

            when(listName){
                "Regular" -> selectedList = apiResponseDto.apps.Regular
                "Amoled" -> selectedList = apiResponseDto.apps.AMOLED
                "Regular_Cloned" -> selectedList = apiResponseDto.apps.Regular_Cloned
                "Amoled_Cloned" -> selectedList = apiResponseDto.apps.AMOLED_Cloned
                "Lite" -> selectedList = apiResponseDto.apps.Lite
            }

            val file = context.getSpotifyAPK()

            val request = Request.Builder()
                .url(selectedList[index].link)
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body
                return@withContext responseBody.downloadFileWithProgress(file)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return emptyFlow()
    }
}