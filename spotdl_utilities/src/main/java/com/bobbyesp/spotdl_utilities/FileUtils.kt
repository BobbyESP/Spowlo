package com.bobbyesp.spotdl_utilities

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {
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
}