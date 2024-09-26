package com.bobbyesp.spowlo

import android.app.Application
import android.content.ClipboardManager
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import androidx.core.content.getSystemService
import com.bobbyesp.spowlo.di.appModules
import com.bobbyesp.spowlo.features.spotify.auth.CredentialsStorer
import com.bobbyesp.spowlo.features.spotify.di.spotifyModule
import com.bobbyesp.spowlo.features.spotify.di.spotifyRepositoriesModule
import com.bobbyesp.utilities.Theme
import com.google.android.material.color.DynamicColors
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        MMKV.initialize(this)
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModules, spotifyModule, spotifyRepositoriesModule, appViewModels)
        }
        packageInfo = packageManager.run {
            if (Build.VERSION.SDK_INT >= 33) getPackageInfo(
                packageName, PackageManager.PackageInfoFlags.of(0)
            ) else
                getPackageInfo(packageName, 0)
        }
        applicationScope = CoroutineScope(SupervisorJob())
        clipboard = getSystemService()!!
        connectivityManager = getSystemService()!!
        CredentialsStorer.initializeCredentials(this)
        Theme.applicationScope = applicationScope
        DynamicColors.applyToActivitiesIfAvailable(this)
        super.onCreate()
    }

    companion object {
        lateinit var clipboard: ClipboardManager
        lateinit var applicationScope: CoroutineScope
        lateinit var connectivityManager: ConnectivityManager
        lateinit var packageInfo: PackageInfo

        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }

        const val APP_PACKAGE_NAME = "com.bobbyesp.spowlo"
        const val APP_FILE_PROVIDER = "$APP_PACKAGE_NAME.fileprovider"
    }
}