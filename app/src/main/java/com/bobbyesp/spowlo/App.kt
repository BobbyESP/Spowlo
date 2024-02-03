package com.bobbyesp.spowlo

import android.annotation.SuppressLint
import android.app.Application
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
import android.util.Log
import androidx.core.content.getSystemService
import com.bobbyesp.ffmpeg.FFmpeg
import com.bobbyesp.library.SpotDL
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.SpotifyAuthManager
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.utils.files.FilesUtil
import com.bobbyesp.spowlo.utils.preferences.PreferencesStrings.DOWNLOAD_DIR
import com.bobbyesp.spowlo.utils.preferences.PreferencesUtil
import com.bobbyesp.spowlo.utils.preferences.PreferencesUtil.getString
import com.bobbyesp.spowlo.utils.services.DownloaderKeepUpService
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
        audioDownloadDir = DOWNLOAD_DIR.getString(
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                getString(R.string.app_name)
            ).absolutePath
        )
        clipboard = getSystemService()!!
        connectivityManager = getSystemService()!!
        appContext = applicationContext
        applicationScope.launch((Dispatchers.Main)) {
            try {
                SpotDL.getInstance().init(context)
                FFmpeg.init(context)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.i("App", "SpotDL init failed", e)
                    val logfile = createLogFile(this@App, e.stackTraceToString())
                    startCrashReportActivity(logfile)
                }
            }
        }
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            val stackTrace = e.stackTraceToString()
//            if(stackTrace.contains("contained an invalid tag (zero)")) {
//                Log.i("App", "Spotify API dependency credentials file is invalid or broken; going to delete it and restart the activity")
//                val spAuthManager by lazy { SpotifyAuthManagerImpl(context) }
//                deleteEncryptedSharedPrefs(spAuthManager)
//                val intent = Intent(context, SpCredsCrashRestartActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                context.startActivity(intent)
//                return@setDefaultUncaughtExceptionHandler
//            } else {
//                Log.i("App", "Uncaught exception", e)
//            }
            val logfile = createLogFile(this, stackTrace)
            startCrashReportActivity(logfile)
        }
    }

    private fun deleteEncryptedSharedPrefs(spAuthManager: SpotifyAuthManager): Boolean {
        try {
            spAuthManager.deleteCredentials()
            return true
        } catch (e: Exception) {
            Log.e(
                "App",
                "Error deleting encrypted shared prefs directly using the Spotify wrapper library. Trying other way..."
            )
        }

        return try {
            FilesUtil.SharedPreferences.deleteSharedPreferences(this@App)
            true
        } catch (e: Exception) {
            Log.e("App", "Error deleting encrypted shared prefs file", e)
            false
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
        @SuppressLint("StaticFieldLeak")
        lateinit var appContext: Context
        private const val PRIVATE_DIRECTORY_SUFFIX = ".Spowlo"
        lateinit var clipboard: ClipboardManager
        lateinit var audioDownloadDir: String
        lateinit var applicationScope: CoroutineScope
        lateinit var connectivityManager: ConnectivityManager
        lateinit var packageInfo: PackageInfo
        const val USER_AGENT_HEADER =
            "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Mobile Safari/537.36 Edg/105.0.1343.53"
        const val SPOTIFY_LOGO_URL =
            "https://www.liderlogo.es/wp-content/uploads/2022/12/pasted-image-0-4-1024x576.png"

        var isKeepUpServiceRunning = false

        private val connection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                val binder = service as DownloaderKeepUpService.DownloadServiceBinder
                isKeepUpServiceRunning = true
            }

            override fun onServiceDisconnected(arg0: ComponentName) {
            }
        }

        fun updateDownloadDir(path: String) {
            audioDownloadDir = path
            PreferencesUtil.encodeString(DOWNLOAD_DIR, path)
        }


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

        fun startKeepUpService(context: Context) {
            if (isKeepUpServiceRunning) return
            Intent(context.applicationContext, DownloaderKeepUpService::class.java).also { intent ->
                context.applicationContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }

        fun stopKeepUpService(context: Context) {
            if (!isKeepUpServiceRunning) return
            try {
                isKeepUpServiceRunning = false
                context.applicationContext.run {
                    unbindService(connection)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}