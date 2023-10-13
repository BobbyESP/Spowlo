package com.bobbyesp.spowlo.features.mod_downloader.data.remote

import android.content.Context
import androidx.annotation.CheckResult
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.features.mod_downloader.domain.model.APIResponseDto
import com.bobbyesp.spowlo.features.mod_downloader.domain.model.ApkResponseDto
import com.bobbyesp.spowlo.utils.UpdateUtil
import com.bobbyesp.spowlo.utils.UpdateUtil.downloadFileWithProgress
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
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
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface ModsDownloaderAPIService {
    suspend fun getAPIResponse(): Result<APIResponseDto>
    suspend fun downloadPackage(
        context: Context = App.context): Flow<UpdateUtil.DownloadStatus>

    companion object {
        fun create(): ModsDownloaderAPIService = ModsDownloaderAPIImpl(
            client = HttpClient(Android) {
                engine {
                    sslManager = {
                        it.setHostnameVerifier { _, _ -> true } //Caution, this is kind of dangerous/unsecure
                    }
                }
                install(Logging) {
                    level = LogLevel.ALL
                }
                install(ContentNegotiation) {
                    json(
                        contentType = ContentType.Application.Json,
                        json = Json {
                            ignoreUnknownKeys = true
                            encodeDefaults = true
                        }
                    )
                }
            }
        )
    }
}