package com.bobbyesp.spowlo

import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import androidx.core.content.getSystemService
import com.bobbyesp.utilities.utilities.Theme
import com.google.android.material.color.DynamicColors
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        MMKV.initialize(this)
        packageInfo = packageManager.run {
            if (Build.VERSION.SDK_INT >= 33) getPackageInfo(
                packageName, PackageManager.PackageInfoFlags.of(0)
            ) else
                getPackageInfo(packageName, 0)
        }
        context = applicationContext
        applicationScope = CoroutineScope(SupervisorJob())
        clipboard = getSystemService()!!
        connectivityManager = getSystemService()!!

        Theme.applicationScope = applicationScope
        DynamicColors.applyToActivitiesIfAvailable(this)
        super.onCreate()
    }

    companion object {
        @ApplicationContext
        lateinit var context: Context
        lateinit var clipboard: ClipboardManager
        lateinit var applicationScope: CoroutineScope
        lateinit var connectivityManager: ConnectivityManager
        lateinit var packageInfo: PackageInfo

        const val APP_PACKAGE_NAME = "com.bobbyesp.spowlo"
        const val APP_FILE_PROVIDER = "$APP_PACKAGE_NAME.fileprovider"
    }
}