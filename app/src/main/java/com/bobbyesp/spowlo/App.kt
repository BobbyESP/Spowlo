package com.bobbyesp.spowlo

import android.annotation.SuppressLint
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.core.content.getSystemService
import com.bobbyesp.ffmpeg.FFmpeg
import com.bobbyesp.library.SpotDL
import com.bobbyesp.spowlo.utils.AUDIO_DIRECTORY
import com.bobbyesp.spowlo.utils.DownloaderUtil
import com.bobbyesp.spowlo.utils.EXTRA_DIRECTORY
import com.bobbyesp.spowlo.utils.FilesUtil
import com.bobbyesp.spowlo.utils.FilesUtil.createEmptyFile
import com.bobbyesp.spowlo.utils.FilesUtil.getCookiesFile
import com.bobbyesp.spowlo.utils.NotificationsUtil
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.PreferencesUtil.getString
import com.bobbyesp.spowlo.utils.ToastUtil
import com.google.android.material.color.DynamicColors
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        context = applicationContext
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

        applicationScope.launch((Dispatchers.IO)) {
            try {
                SpotDL.getInstance().init(this@App)
                FFmpeg.init(this@App)
                DownloaderUtil.getCookiesContentFromDatabase().getOrNull()?.let {
                    FilesUtil.writeContentToFile(it, getCookiesFile())
                }
            } catch (e: Exception) {
                Looper.prepare()
                ToastUtil.makeToast(text = e.message ?: "Unknown error")
                e.printStackTrace()
                clipboard.setPrimaryClip(ClipData.newPlainText(null, e.message))
            }
        }
        audioDownloadDir = AUDIO_DIRECTORY.getString(
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                getString(R.string.app_name)
            ).absolutePath
        )
        extraDownloadDir = EXTRA_DIRECTORY.getString(
            ""
        )
        if (Build.VERSION.SDK_INT >= 26) NotificationsUtil.createNotificationChannel()
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            val stackTrace = e.stackTraceToString()
            val logfile = createLogFile(this, stackTrace)
            startCrashReportActivity(logfile)
        }
    }

    private fun startCrashReportActivity(logfilePath: String) {
        val intent = Intent(this, CrashHandlerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("version_report", getVersionReport())
            putExtra("logfile_path", logfilePath)
        }
        startActivity(intent)
    }

    companion object {
        private const val PRIVATE_DIRECTORY_SUFFIX = ".Spowlo"
        lateinit var clipboard: ClipboardManager
        lateinit var audioDownloadDir: String
        lateinit var extraDownloadDir: String
        lateinit var applicationScope: CoroutineScope
        lateinit var connectivityManager: ConnectivityManager
        lateinit var packageInfo: PackageInfo
        val SpotDl = SpotDL.getInstance()
        val FFMPEG = FFmpeg.getInstance()
        const val userAgentHeader =
            "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Mobile Safari/537.36 Edg/105.0.1343.53"

        var isServiceRunning = false

        private val connection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                val binder = service as DownloaderKeepUpService.DownloadServiceBinder
                isServiceRunning = true
            }

            override fun onServiceDisconnected(arg0: ComponentName) {

            }
        }

        fun startService() {
            if (isServiceRunning) return
            Intent(context.applicationContext, DownloaderKeepUpService::class.java).also { intent ->
                context.applicationContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }

        fun stopService() {
            if (!isServiceRunning) return
            try {
                isServiceRunning = false
                context.applicationContext.run {
                    unbindService(connection)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        fun getPrivateDownloadDirectory(): String =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).resolve(
                PRIVATE_DIRECTORY_SUFFIX
            ).run {
                createEmptyFile(".nomedia")
                absolutePath
            }


        fun updateDownloadDir(path: String) {
            audioDownloadDir = path
            PreferencesUtil.encodeString(AUDIO_DIRECTORY, path)
        }

        fun getVersionReport(): String {
            val versionName = packageInfo.versionName
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
            return StringBuilder().append("App version: $versionName ($versionCode)\n")
                .append("Device information: Android $release (API ${Build.VERSION.SDK_INT})\n")
                .append("Supported ABIs: ${Build.SUPPORTED_ABIS.contentToString()}\n")
                .append("spotDL version: ${SpotDl.version(context.applicationContext)}\n")
                .toString()
        }

        fun createLogFile(context: Context, errorReport: String): String {
            val date = getDayAsString()
            val fileName = "log_$date.txt"
            val logFile = File(context.filesDir, fileName)
            if (!logFile.exists()) {
                logFile.createNewFile()
            }
            logFile.appendText(errorReport)
            return logFile.absolutePath
        }

        private fun getDayAsString(): String {
            val calendar = Calendar.getInstance()
            return "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${
                calendar.get(
                    Calendar.YEAR
                )
            }"
        }

        fun isFDroidBuild(): Boolean = packageInfo.versionName.contains("F-Droid")

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}