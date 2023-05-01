package com.bobbyesp.appmodules.core.ext

import android.content.Context
import java.io.File
import javax.inject.Singleton

@Singleton
object ConfigFilesDefs {
    fun getCredentialsFile(appContext: Context) =
        File(appContext.filesDir, "spa_creds")

    fun getCacheDir(appContext: Context) =
        File(appContext.cacheDir, "spa_cache")


}