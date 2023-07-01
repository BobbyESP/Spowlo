package com.bobbyesp.spotdl_android.feats.whl_downloader.data.remote

import com.bobbyesp.spotdl_android.data.SpotDLException
import com.bobbyesp.spotdl_android.feats.whl_downloader.data.model.PyPiResponse
import com.bobbyesp.spotdl_utilities.preferences.SPOTDL_VERSION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object WhlDownloader {

    private val client = OkHttpClient()

    private val jsonFormatter = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    @Throws(SpotDLException::class, IOException::class)
    suspend fun downloadWhl(
        url: String,
        onProgress: (Int) -> Unit,
        onComplete: (File) -> Unit,
        onError: (String) -> Unit,
    ): Flow<WhlDownloaderState> = withContext(Dispatchers.IO) {
        flow {
            val request = okhttp3.Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onError(e.message ?: "Unknown error")
                }

                override fun onResponse(call: Call, response: Response) {
                    launch {
                        if (response.isSuccessful) {
                            val body = response.body
                            val fileToSave = File.createTempFile("spotdl", ".whl")
                            val fileFlow = body.downloadFileWithProgress(fileToSave)
                            fileFlow
                                .flowOn(Dispatchers.IO)
                                .distinctUntilChanged()
                                .collect { state ->
                                    when (state) {
                                        is WhlDownloaderState.Downloading -> {
                                            onProgress(state.progress)
                                        }
                                        is WhlDownloaderState.Downloaded -> {
                                            onComplete(state.whlFile)
                                        }
                                        is WhlDownloaderState.Error -> {
                                            onError(state.message)
                                        }

                                        else -> {

                                        }
                                    }
                                }
                        } else {
                            onError("Failed to download .whl file")
                        }
                    }
                }
            })
        }
    }

    suspend fun checkForUpdate(
        projectName: String,
    ): PyPiResponse? {
        return suspendCoroutine { continuation ->
            val request = okhttp3.Request.Builder()
                .url("https://pypi.org/pypi/$projectName/json")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWith(Result.failure(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    if(response.isSuccessful){
                        val body = response.body.string()
                        val pyPiResponse = jsonFormatter.decodeFromString(
                            PyPiResponse.serializer(),
                            body
                        )
                        checkVersions(pyPiResponse.info.version)

                        continuation.resumeWith(Result.success(pyPiResponse))
                    } else {
                        continuation.resumeWithException(SpotDLException("Failed to check for updates. Response code: ${response.code}"))
                    }
                }
            })
        }
    }

    private fun ResponseBody.downloadFileWithProgress(fileToSave: File): Flow<WhlDownloaderState> = flow {
        emit(WhlDownloaderState.Downloading(0))

        var deleteFile = true

        try {
            byteStream().use { inputStream ->
                fileToSave.outputStream().use { outputStream ->
                    val totalBytes = contentLength()
                    val data = ByteArray(8_192)
                    var progressBytes = 0L

                    while (true) {
                        val bytes = inputStream.read(data)

                        if (bytes == -1) {
                            break
                        }

                        outputStream.channel
                        outputStream.write(data, 0, bytes)
                        progressBytes += bytes
                        emit(WhlDownloaderState.Downloading(((progressBytes * 100) / totalBytes).toInt()))
                    }

                    when {
                        progressBytes < totalBytes -> throw Exception("Missing bytes from the download!")
                        progressBytes > totalBytes -> throw Exception("Too many bytes from the download!")
                        else -> deleteFile = false
                    }
                }
            }

            emit(WhlDownloaderState.Downloaded(fileToSave))
        } finally {
            if (deleteFile) {
                fileToSave.delete()
            }
        }
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    fun checkVersions(newVersion: String): Boolean {
        val currentVersion = SPOTDL_VERSION
        return currentVersion != newVersion
    }
}

sealed class WhlDownloaderState {
    object NotDownloading : WhlDownloaderState()
    data class Downloading(val progress: Int) : WhlDownloaderState()
    data class Downloaded(val whlFile: File) : WhlDownloaderState()
    data class Error(val message: String) : WhlDownloaderState()
}