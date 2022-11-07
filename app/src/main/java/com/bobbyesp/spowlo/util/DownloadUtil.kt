package com.bobbyesp.spowlo.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bobbyesp.spowlo.Spowlo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File

object DownloadUtil {

    private const val TAG = "DownloadUtil"
    private var apkUrl: String = ""

    private val client = OkHttpClient()

    private fun Context.getLatestApk() =
        File(getExternalFilesDir("apk"), "latest.apk")

    suspend fun downloadSpotify(apkUrl: String, context: Context = Spowlo.context,): Flow<DownloadStatus> = withContext(Dispatchers.IO){
        val request = Request.Builder().url(apkUrl).build()
        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body
            return@withContext responseBody.downloadFileWithProgress(context.getLatestApk())
        }catch (e: Exception) {
            e.printStackTrace()
        }
        emptyFlow()
    }

    private fun ResponseBody.downloadFileWithProgress(saveFile: File): Flow<DownloadStatus> = flow {
        emit(DownloadStatus.Progress(0))

        var deleteFile = true

        try {
            byteStream().use { inputStream ->
                saveFile.outputStream().use { outputStream ->
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
                        emit(DownloadStatus.Progress(percent = ((progressBytes * 100) / totalBytes).toInt()))
                    }

                    when {
                        progressBytes < totalBytes -> throw Exception("missing bytes")
                        progressBytes > totalBytes -> throw Exception("too many bytes")
                        else -> deleteFile = false
                    }
                }
            }

            emit(DownloadStatus.Finished(saveFile))
        } finally {
            if (deleteFile) {
                saveFile.delete()
            }
        }
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    fun openLinkInBrowser(link: String) {
        //open a browser with the link
        println("--------------------------------------------------------------------------")
        println(link)
        //add flag FLAG_ACTIVITY_NEW_TASK to open the browser in a new task
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        Spowlo.context.startActivity(intent)
    }

    sealed class DownloadStatus {
        object NotYet : DownloadStatus()
        data class Progress(val percent: Int) : DownloadStatus()
        data class Finished(val file: File) : DownloadStatus()
    }
}