package com.bobbyesp.spowlo.utils.files

import android.content.Context
import android.util.Log
import java.io.File

object FilesUtil {
    fun readFile(file: File): String {
        return file.readText()
    }

    object SharedPreferences {
        fun deleteSharedPreferences(context: Context): Boolean {
            val sharedPrefsFile = File(context.applicationInfo.dataDir + "/shared_prefs")

            return try {
                if (sharedPrefsFile.exists() && sharedPrefsFile.isDirectory) {
                    val files = sharedPrefsFile.listFiles()

                    files?.let {
                        for (file in files) {
                            file.delete()
                        }
                    }
                }

                true
            } catch (e: Exception) {
                Log.e("FilesUtil", "deleteSharedPreferences: ${e.message}", e)
                false
            }
        }
    }
}