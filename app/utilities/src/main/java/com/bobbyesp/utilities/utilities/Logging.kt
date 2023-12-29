package com.bobbyesp.utilities.utilities

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.util.Log
import com.tencent.mmkv.BuildConfig
import java.io.File

object Logging {
    private val callingClass = Throwable().stackTrace[1].className
    val isDebug = BuildConfig.DEBUG
    fun i(message: String) {
        Log.i(callingClass, message)
    }

    fun d(message: String) {
        Log.d(callingClass, message)
    }

    fun e(message: String) {
        Log.e(callingClass, message)
    }

    fun e(throwable: Throwable) {
        Log.e(callingClass, throwable.message ?: "No message", throwable)
    }

    fun e(message: String, throwable: Throwable) {
        Log.e(callingClass, message, throwable)
    }

    fun w(message: String) {
        Log.w(callingClass, message)
    }

    fun v(message: String) {
        Log.v(callingClass, message)
    }

    fun wtf(message: String) {
        Log.wtf(callingClass, message)
    }

    fun getVersionReport(packageInfo: PackageInfo): String {
        val versionName = packageInfo.versionName

        @Suppress("DEPRECATION")
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
        val release = if (Build.VERSION.SDK_INT >= 30) {
            Build.VERSION.RELEASE_OR_CODENAME
        } else {
            Build.VERSION.RELEASE
        }

        return StringBuilder()
            .append("App version: Clippy $versionName ($versionCode)\n")
            .append("Android version: Android $release (API ${Build.VERSION.SDK_INT})\n")
            .append("Device: ${Build.MANUFACTURER} ${Build.MODEL}\n")
            .append("Supported ABIs: ${Build.SUPPORTED_ABIS.contentToString()}\n")
            .toString()
    }

    fun createLogFile(context: Context, errorReport: String): String {
        val date = Time.getZuluTimeSnapshot()
        val fileName = "log_$date.txt"
        val logFile = File(context.filesDir, fileName)
        if (!logFile.exists()) {
            logFile.createNewFile()
        }
        logFile.appendText(errorReport)
        return logFile.absolutePath
    }
}