package com.bobbyesp.ffmpeg

import android.content.Context
import com.bobbyesp.library.SpotDLException
import com.bobbyesp.spotdl_utilities.SharedPreferencesHelper
import com.bobbyesp.spotdl_utilities.storage.Zip
import org.apache.commons.io.FileUtils
import java.io.File

object FFmpeg {
    private var initialized = false
    private var binDir: File? = null
    private val sharedPreferencesHelper by lazy { SharedPreferencesHelper() }
    private val zipUtils by lazy { Zip() }

    @Synchronized
    fun init(appContext: Context) {
        if (initialized) return
        val baseDir = File(appContext.noBackupFilesDir, baseName)
        if (!baseDir.exists()) baseDir.mkdir()
        binDir = File(appContext.applicationInfo.nativeLibraryDir)
        val packagesDir = File(baseDir, packagesRoot)
        val ffmpegDir = File(packagesDir, ffmegDirName)
        initFFmpeg(appContext, ffmpegDir)
        initialized = true
    }

    private fun initFFmpeg(appContext: Context, ffmpegDir: File) {
        val ffmpegLib = File(binDir, ffmpegLibName)
        // using size of lib as version
        val ffmpegSize = ffmpegLib.length().toString()
        if (!ffmpegDir.exists() || shouldUpdateFFmpeg(appContext, ffmpegSize)) {
            FileUtils.deleteQuietly(ffmpegDir)
            ffmpegDir.mkdirs()
            try {
                zipUtils.unzip(ffmpegLib, ffmpegDir)
            } catch (e: Exception) {
                FileUtils.deleteQuietly(ffmpegDir)
                throw SpotDLException("failed to initialize", e)
            }
            updateFFmpeg(appContext, ffmpegSize)
        }
    }

    private fun shouldUpdateFFmpeg(appContext: Context, version: String): Boolean {
        return version != sharedPreferencesHelper[appContext, ffmpegLibVersion]
    }

    private fun updateFFmpeg(appContext: Context, version: String) {
        sharedPreferencesHelper.update(appContext, ffmpegLibVersion, version)
    }

    @JvmStatic
    fun getInstance() = this

    private const val baseName = "spotdl_android"
    private const val packagesRoot = "packages"
    private const val ffmegDirName = "ffmpeg"
    private const val ffmpegLibName = "libffmpeg.zip.so"
    private const val ffmpegLibVersion = "ffmpegLibVersion"

}