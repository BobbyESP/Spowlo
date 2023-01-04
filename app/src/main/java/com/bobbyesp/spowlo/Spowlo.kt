package com.bobbyesp.spowlo

import android.annotation.SuppressLint
import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Environment
import com.bobbyesp.ffmpeg.FFmpeg
import com.bobbyesp.library.SpotDL
import com.bobbyesp.spowlo.database.CommandTemplate
import com.bobbyesp.spowlo.util.DatabaseUtil
import com.bobbyesp.spowlo.util.FilesUtil.createEmptyFile
import com.bobbyesp.spowlo.util.PreferencesUtil
import com.bobbyesp.spowlo.util.PreferencesUtil.AUDIO_DIRECTORY
import com.bobbyesp.spowlo.util.PreferencesUtil.TEMPLATE_INDEX
import com.google.android.material.color.DynamicColors
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class Spowlo : Application() {

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        context = applicationContext
        applicationScope = CoroutineScope(SupervisorJob())
        DynamicColors.applyToActivitiesIfAvailable(this)

        clipboard = getSystemService(ClipboardManager::class.java)
        connectivityManager = getSystemService(ConnectivityManager::class.java)

        applicationScope.launch((Dispatchers.IO)) {
            if (!PreferencesUtil.containsKey(TEMPLATE_INDEX)) {
                PreferencesUtil.updateInt(TEMPLATE_INDEX, 0)
                DatabaseUtil.insertTemplate(
                    CommandTemplate(
                        0,
                        context.getString(R.string.custom_command_template),
                        PreferencesUtil.getString(
                            PreferencesUtil.TEMPLATE, context.getString(R.string.template_example)
                        )
                    )
                )
            }
            try {
                SpotDL.getInstance().init(this@Spowlo)
                FFmpeg.getInstance().init(this@Spowlo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val PRIVATE_DIRECTORY_SUFFIX = ".Spowlo"
        private const val TAG = "Spowlo"
        lateinit var applicationScope: CoroutineScope
        lateinit var clipboard: ClipboardManager
        lateinit var audioDownloadDir: String
        var SpotDLVersion = ""
        lateinit var connectivityManager: ConnectivityManager

        fun getPrivateDownloadDirectory(): String =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).resolve(
                PRIVATE_DIRECTORY_SUFFIX
            ).run {
                createEmptyFile(".nomedia")
                absolutePath
            }


        fun updateDownloadDir(path: String) {
            audioDownloadDir = path
            PreferencesUtil.updateString(AUDIO_DIRECTORY, path)
        }

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}