package com.bobbyesp.ffmpeg

import android.content.Context
import com.bobbyesp.ffmpeg.LibsNames.androidLibName
import com.bobbyesp.ffmpeg.exceptions.FFmpegException
import com.bobbyesp.spotdl_utilities.FileUtils
import com.bobbyesp.spotdl_utilities.ZipUtils
import com.bobbyesp.spotdl_utilities.preferences.FFMPEG_VERSION
import com.bobbyesp.spotdl_utilities.preferences.PreferencesUtil.getString
import com.bobbyesp.spotdl_utilities.preferences.PreferencesUtil.updateString
import java.io.File

object FFmpeg {

    private var isInitialized = false

    private lateinit var binariesDirectory: File
    private lateinit var baseDirectory: File

    private lateinit var packagesDirectory: File
    private lateinit var ffmpegDirectory: File

    @Synchronized
    fun init(applicationContext: Context) {
        if (isInitialized) return
        baseDirectory = File(applicationContext.noBackupFilesDir, androidLibName)

        binariesDirectory = File(applicationContext.applicationInfo.nativeLibraryDir)

        if (!baseDirectory.exists()) baseDirectory.mkdir()

        packagesDirectory = File(baseDirectory, LibsNames.packagesRoot)

        ffmpegDirectory = File(packagesDirectory, LibsNames.ffmpegInternalDirectoryName)

        initFFmpeg(ffmpegDirectory)

        isInitialized = true
    }

    private fun initFFmpeg(ffmpegDirectory: File) {
        val ffmpegLib = File(binariesDirectory, LibsNames.ffmpegLibraryName)
        // using size of lib as version
        val ffmpegSize = ffmpegLib.length().toString()
        if (!ffmpegDirectory.exists() || shouldUpdateFFmpeg(ffmpegSize)) {
            FileUtils.deleteFileSilently(ffmpegDirectory)
            ffmpegDirectory.mkdirs()
            try {
                ZipUtils.decompressFile(ffmpegLib, ffmpegDirectory)
            } catch (e: Exception) {
                FileUtils.deleteFileSilently(ffmpegDirectory)
                throw FFmpegException(
                    "An error has occurred while trying to decompress the FFmpeg library.",
                    e
                )
            }
            updateFFmpeg(ffmpegSize)
        }
    }

    private fun shouldUpdateFFmpeg(version: String): Boolean {
        return version != FFMPEG_VERSION.getString()
    }

    private fun updateFFmpeg(version: String) {
        FFMPEG_VERSION.updateString(version)
    }

}