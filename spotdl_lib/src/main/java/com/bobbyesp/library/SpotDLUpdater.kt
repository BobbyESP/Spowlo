package com.bobbyesp.library

import android.content.Context
import android.util.Log
import com.bobbyesp.commonutilities.SharedPrefsHelper
import com.bobbyesp.library.dto.Release
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


open class SpotDLUpdater {

    companion object {
        fun getInstance(): SpotDLUpdater {
            return SpotDLUpdater()
        }
    }

    private val TAG = "SpotDLUpdater"

    private val client = OkHttpClient()

    private val releasesUrl =
        "https://api.github.com/repos/spotDL/spotify-downloader/releases/latest"

    private val spotDLVersionKey = "spotDLVersion"

    private val requestForReleases =
        Request.Builder().url(releasesUrl)
            .build()

    private val jsonFormat = Json { ignoreUnknownKeys = true }

    @Throws(IOException::class, SpotDLException::class)
    suspend fun update(appContext: Context): SpotDL.UpdateStatus {
        val release = checkForUpdate(appContext) ?: return SpotDL.UpdateStatus.ALREADY_UP_TO_DATE
        val downloadUrl = getDownloadUrl(release)
        val updateFile = downloadUpdate(appContext, downloadUrl)
        val spotdlDir = getSpotDLDir(appContext)
        val binary = File(spotdlDir, "spotdl")

        try {
            /*DELETE OLDER VERSION OF THE LIBRARY BINARY*/
            if(spotdlDir.exists()) {
                FileUtils.deleteDirectory(spotdlDir)
            }
            /* Install the downloaded version */
            spotdlDir.mkdir()
            FileUtils.copyFile(updateFile, binary)
            Log.d(TAG, "Library update: ${binary.canExecute()}")
        } catch (e: Exception) {

            FileUtils.deleteQuietly(spotdlDir)
            SpotDL.getInstance().initSpotDL(appContext, spotdlDir)
            throw SpotDLException("Failed to update SpotDL", e)
        } finally {
            updateFile.delete()
        }
        updateSharedPrefs(appContext, getTag(release))
        return SpotDL.UpdateStatus.DONE
    }

    private fun updateSharedPrefs(appContext: Context, tag: String) {
        //change the spotDLVersionKey to the new version tag
        SharedPrefsHelper.update(appContext, spotDLVersionKey, tag)
    }

    //check for updates+
    @Throws(IOException::class, SpotDLException::class)
    private suspend fun checkForUpdate(appContext: Context): Release? {
        /*SpotDL.UpdateStatus*/
        return suspendCoroutine { continuation ->
            //check if the user has the latest version of spotDL and also update the SpotDL.UpdateStatus
            client.newCall(requestForReleases).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    if (response.isSuccessful) {
                        val release = jsonFormat.decodeFromString<Release>(response.body!!.string())
                        val newVersion = getTag(release)
                        val oldVersion = SharedPrefsHelper[appContext, spotDLVersionKey]

                        Log.d(TAG, "New version: $newVersion, old version: $oldVersion")
                        if(newVersion == oldVersion) {
                            Log.d(TAG, "No necessary update :)")
                            continuation.resume(null)
                        } else {
                            Log.d(TAG, "A new version is available: $newVersion")
                            continuation.resume(release)
                        }
                    } else {
                        continuation.resumeWithException(SpotDLException("Failed to check for updates. Response code: ${response.code}"))
                    }
                }
            })
        }
    }

    private fun downloadUpdate(appContext: Context ,downloadUrl: String): File {
        val updateFile = File.createTempFile("spotdl", null, appContext.cacheDir)
        FileUtils.copyURLToFile(URL(downloadUrl), updateFile, 10000, 10000)
        return updateFile
    }

    private fun getTag(release: Release): String {
        return release.tag_name
    }

    @Throws(SpotDLException::class)
    private fun getDownloadUrl(release: Release): String {
        var downloadUrl = ""
        release.assets.forEach { asset ->
            if (asset.name == "spotDL") {
                downloadUrl = asset.browser_download_url
                return@forEach
            }
        }

        if (downloadUrl.isEmpty())
            throw SpotDLException("No downloadable spotDL binary URL was found. The release DTO result was: $release")

        Log.d("SpotDLUpdater", "Download URL: $downloadUrl")
        return downloadUrl
    }

    private fun getSpotDLDir(appContext: Context): File {
        val baseDir = File(appContext.noBackupFilesDir, SpotDL.getInstance().baseName)
        return File(baseDir, SpotDL.getInstance().spotdlDirName)
    }

    open fun version(appContext: Context): String? {
        return SharedPrefsHelper[appContext, spotDLVersionKey]
    }
}