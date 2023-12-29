package com.bobbyesp.utilities.utilities

import android.content.Context
import java.io.File

object Files {
    fun getTempDirectory(context: Context): File {
        return context.cacheDir
    }

    fun createTempFile(context: Context, prefix: String, suffix: String): File {
        return File.createTempFile(prefix, suffix, getTempDirectory(context))
    }

    fun deleteTempFile(context: Context, prefix: String, suffix: String) {
        val tempFile = File(getTempDirectory(context), prefix + suffix)
        tempFile.delete()
    }

    fun deleteTempFile(context: Context, fileName: String) {
        val tempFile = File(getTempDirectory(context), fileName)
        tempFile.delete()
    }
}