package com.bobbyesp.spowlo

import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import androidx.core.content.getSystemService
import com.bobbyesp.ffmpeg.FFmpeg
import com.bobbyesp.library.SpotDL
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.utils.time.TimeUtils
import com.google.android.material.color.DynamicColors
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    @ApplicationContext
    lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        Route.initialize(this)
        packageInfo = packageManager.run {
            if (Build.VERSION.SDK_INT >= 33) getPackageInfo(
                packageName, PackageManager.PackageInfoFlags.of(0)
            ) else
                getPackageInfo(packageName, 0)
        }
        applicationScope = CoroutineScope(SupervisorJob())
        DynamicColors.applyToActivitiesIfAvailable(this)

        clipboard = getSystemService()!!
        connectivityManager = getSystemService()!!

        applicationScope.launch((Dispatchers.Main)) {
            try {
                SpotDL.getInstance().init(context)
                FFmpeg.init(context)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val logfile = createLogFile(this@App, e.stackTraceToString())
                    startCrashReportActivity(logfile)
                }
            }
        }
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            val logfile = createLogFile(this, e.stackTraceToString())
            startCrashReportActivity(logfile)
        }
    }

    private fun startCrashReportActivity(logfilePath: String) {
        startActivity(Intent(this, CrashHandlerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("version_report", getVersionReport())
            putExtra("logfile_path", logfilePath)
        })
    }

    companion object {
        lateinit var clipboard: ClipboardManager
        lateinit var applicationScope: CoroutineScope
        lateinit var connectivityManager: ConnectivityManager
        lateinit var packageInfo: PackageInfo
        const val userAgentHeader =
            "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Mobile Safari/537.36 Edg/105.0.1343.53"
        const val SpotifyLogoUrl = "https://www.liderlogo.es/wp-content/uploads/2022/12/pasted-image-0-4-1024x576.png"

        fun getVersionReport(): String {
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

            return StringBuilder().append("App version: Spowlo $versionName ($versionCode)\n")
                .append("Android version: Android $release (API ${Build.VERSION.SDK_INT})\n")
                .append("Device: ${Build.MANUFACTURER} ${Build.MODEL}\n")
                .append("Supported ABIs: ${Build.SUPPORTED_ABIS.contentToString()}\n")
                //.append("spotDL version: ${SpotDl.version(context.applicationContext)}\n")
                .toString()
        }

        fun createLogFile(context: Context, errorReport: String): String {
            val date = TimeUtils.getDateWithTimeAsString()
            val fileName = "log_$date.txt"
            val logFile = File(context.filesDir, fileName)
            if (!logFile.exists()) {
                logFile.createNewFile()
            }
            logFile.appendText(errorReport)
            return logFile.absolutePath
        }
    }
}