package com.bobbyesp.utilities.utilities.storage

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object StorageHelper {
    object Compose

    suspend fun writeTextToFile(context: Context, text: String, fileUri: Uri?) {
        withContext(Dispatchers.IO) {
            if (fileUri != null) {
                val documentFile = DocumentFile.fromSingleUri(context, fileUri)
                if (documentFile != null && documentFile.canWrite()) {
                    val outputStream = context.contentResolver.openOutputStream(fileUri)
                    outputStream?.bufferedWriter().use { writer ->
                        writer?.write(text)
                    }
                    outputStream?.close()
                }
            }
        }
    }
}