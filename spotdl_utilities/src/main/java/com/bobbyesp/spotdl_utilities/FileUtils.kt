package com.bobbyesp.spotdl_utilities

import android.content.Context
import android.util.Log
import com.tencent.mmkv.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

object FileUtils {
    private const val TAG = "FileUtils"
    const val isDebug = BuildConfig.DEBUG
    fun deleteFileSilently(file: File): Boolean {
        return try {
            if (file.isDirectory) {
                file.listFiles()?.forEach { deleteFileSilently(it) }
            }
            file.delete()
        } catch (e: Exception) {
            false
        }
    }

    fun copyInputStreamToFile(inputStream: InputStream, outputFile: File) {
        FileOutputStream(outputFile).use { outputStream ->
            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
        }
    }

    fun copyFile(source: File, destination: File) {
        source.copyTo(destination, true)
    }

    fun deleteDirectory(directory: File): Boolean {
        return try {
            if (directory.isDirectory) {
                directory.listFiles()?.forEach { deleteDirectory(it) }
            }
            directory.delete()
        } catch (e: Exception) {
            false
        }
    }

    fun copyRawResourceToFile(context: Context, resourceId: Int, file: File) {
        val inputStream = context.resources.openRawResource(resourceId)
        val outputStream = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var read = inputStream.read(buffer)
        while (read != -1) {
            outputStream.write(buffer, 0, read)
            read = inputStream.read(buffer)
        }
        outputStream.close()
        inputStream.close()
    }

    fun copyURLToFile(url: String, file: File) {
        val inputStream = URL(url).openStream()
        val outputStream = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var read = inputStream.read(buffer)
        while (read != -1) {
            outputStream.write(buffer, 0, read)
            read = inputStream.read(buffer)
        }
        outputStream.close()
        inputStream.close()
    }

    fun copyURLToFile(url: String, file: File, timeout: Int, fileReadTimeout: Int) {
        val inputStream = URL(url).openConnection().apply {
            connectTimeout = timeout
            readTimeout = fileReadTimeout
        }.getInputStream()
        val outputStream = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var read = inputStream.read(buffer)
        while (read != -1) {
            outputStream.write(buffer, 0, read)
            read = inputStream.read(buffer)
        }
        outputStream.close()
        inputStream.close()
    }

    /**
     * Get the list of files in a directory.
     *
     * @param directory The directory to search.
     * @return The list of files in the directory.
     */
    private fun getListOfFilesInDirectory(directory: File): List<File> {
        val files = mutableListOf<File>()
        directory.listFiles()?.let {
            for (file in it) {
                if (file.isDirectory) {
                    files.addAll(getListOfFilesInDirectory(file))
                } else {
                    files.add(file)
                }
            }
        }
        if (isDebug) Log.i(TAG, "FILES: $files")
        return files
    }
}