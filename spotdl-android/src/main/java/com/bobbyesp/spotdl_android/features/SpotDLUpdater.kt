package com.bobbyesp.spotdl_android.features

import android.content.Context
import android.util.Log
import com.bobbyesp.spotdl_android.SpotDL
import com.bobbyesp.spotdl_android.androidLibName
import com.bobbyesp.spotdl_android.data.SpotDLException
import com.bobbyesp.spotdl_android.domain.model.Release
import com.bobbyesp.spotdl_android.spotdlBinaryName
import com.bobbyesp.spotdl_android.spotdlInternalDirectoryName
import com.bobbyesp.spotdl_utilities.preferences.PreferencesUtil.getString
import com.bobbyesp.spotdl_utilities.preferences.PreferencesUtil.updateString
import com.bobbyesp.spotdl_utilities.preferences.SPOTDL_VERSION
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.io.FileUtils.copyFile
import org.apache.commons.io.FileUtils.copyURLToFile
import org.apache.commons.io.FileUtils.deleteDirectory
import org.apache.commons.io.FileUtils.deleteQuietly
import java.io.File
import java.io.IOException
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


open class SpotDLUpdater {

    private val TAG = "SpotDLUpdater"

    private val client = OkHttpClient()

    private val releasesUrl =
        "https://api.github.com/repos/spotDL/spotify-downloader/releases/latest"

    private val requestForReleases = Request.Builder().url(releasesUrl).build()

    private val jsonFormat = Json { ignoreUnknownKeys = true }

    @Throws(IOException::class, SpotDLException::class)
    suspend fun update(appContext: Context): UpdateStatus {
        val release = checkForUpdate() ?: return UpdateStatus.ALREADY_UP_TO_DATE
        val downloadUrl = getDownloadUrl(release)
        val updateFile = downloadUpdate(appContext, downloadUrl)
        val spotdlDir = getSpotDLDir(appContext)
        val binary = File(spotdlDir, spotdlBinaryName)

        try {/*DELETE OLDER VERSION OF THE LIBRARY BINARY*/
            if (spotdlDir.exists()) {
                deleteDirectory(spotdlDir)
            }/* Install the downloaded version */
            spotdlDir.mkdir()
            copyFile(updateFile, binary)
            Log.d(TAG, "Library update: ${binary.canExecute()}")
        } catch (e: Exception) {

            deleteQuietly(spotdlDir)
            SpotDL.getInstance().initializeSpotDL(appContext, spotdlDir)
            throw SpotDLException("Failed to update SpotDL", e)
        } finally {
            updateFile.delete()
        }
        updateSharedPrefs(release.getTag())
        return UpdateStatus.DONE
    }

    private fun updateSharedPrefs(tag: String) {
        SPOTDL_VERSION.updateString(tag)
    }

    @Throws(IOException::class, SpotDLException::class)
    private suspend fun checkForUpdate(): Release? {/*SpotDL.UpdateStatus*/
        return suspendCoroutine { continuation ->
            //check if the user has the latest version of spotDL and also update the SpotDL.UpdateStatus
            client.newCall(requestForReleases).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    if (response.isSuccessful) {
                        val release = jsonFormat.decodeFromString<Release>(response.body.string())
                        val newVersion = release.getTag()
                        val oldVersion = version()

                        Log.d(TAG, "New version: $newVersion, old version: $oldVersion")
                        if (newVersion == oldVersion) {
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

    private fun downloadUpdate(appContext: Context, downloadUrl: String): File {
        val updateFile = File.createTempFile(spotdlBinaryName, null, appContext.cacheDir)
        copyURLToFile(URL(downloadUrl), updateFile, 10000, 10000)
        return updateFile
    }

    private fun Release.getTag(): String {
        return this.tag_name
    }

    @Throws(SpotDLException::class)
    private fun getDownloadUrl(release: Release): String {
        lateinit var downloadUrl: String
        release.assets.forEach { asset ->
            if (asset.name == "spotDL") {
                downloadUrl = asset.browser_download_url
                return@forEach
            }
        }

        if (downloadUrl.isEmpty()) throw SpotDLException("No downloadable spotDL binary URL was found. The release DTO result was: $release")

        Log.d("SpotDLUpdater", "Download URL: $downloadUrl")
        return downloadUrl
    }

    private fun getSpotDLDir(appContext: Context): File {
        val baseDir = File(appContext.noBackupFilesDir, androidLibName)
        return File(baseDir, spotdlInternalDirectoryName)
    }

    open fun version(): String? {
        return SPOTDL_VERSION.getString()
    }

    companion object {
        enum class UpdateStatus {
            DONE, ALREADY_UP_TO_DATE
        }

        @JvmStatic
        fun getInstance(): SpotDLUpdater {
            return SpotDLUpdater()
        }
    }
}